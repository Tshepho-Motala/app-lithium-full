package lithium.service.cashier.processor.checkout.cc;

import com.checkout.CheckoutApi;
import com.checkout.CheckoutApiImpl;
import com.checkout.payments.CustomerRequest;
import com.checkout.payments.PaymentRequest;
import com.checkout.payments.PaymentResponse;
import com.checkout.payments.ThreeDSRequest;
import com.checkout.payments.TokenSource;
import lithium.config.LithiumConfigurationProperties;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.data.AddCardRequest;
import lithium.service.cashier.processor.checkout.cc.data.AddCardResponse;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutInitializeData;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/public")
@Slf4j
public class PaymentController {
	@Autowired
	LithiumConfigurationProperties lithiumProperties;
	@Autowired
	CashierDoCallbackService service;
	@Autowired
	CashierInternalClientService cashier;
	@Autowired
	LithiumServiceClientFactory serviceFactory;

	@GetMapping("/usedcards")
	public List<UserCard> getUsedCards(
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil token,
			HttpServletRequest request
	) throws Status500InternalServerErrorException	{
		try {
			log.debug("Get user used cards for: [user=" + token.guid() + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + "]");
			return cashier.getUserCards("checkout-cc", true,
					token.username(), token.domainName(), token.guid(),
					request.getRemoteAddr(), userAgent);
		} catch (Exception e) {
			log.error("Failed get checkout initialization data.", e);
			throw new Status500InternalServerErrorException("Failed to get user cards.");
		}
	}

	@GetMapping("/initialize/data")
	public CheckoutInitializeData getInitData(
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil token,
			HttpServletRequest request
	) throws Status500InternalServerErrorException, Status400BadRequestException {
		try {
			log.debug("Get user init data for: [user=" + token.guid() + " userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + "]");
			String publicKey = cashier.propertyOfFirstEnabledProcessor("public_key",
					"checkout-cc", true, token.guid(), token.domainName(),
					request.getRemoteAddr(), request.getHeader("User-Agent")).getValue();

			return CheckoutInitializeData.builder().publicKey(publicKey).build();
		} catch (Exception e) {
			log.error("Failed get checkout initialization data.", e);
			if (e instanceof Status400BadRequestException) {
				throw new Status400BadRequestException("Payment method is not supported.");
			}
			throw new Status500InternalServerErrorException("Failed to get user cards.");
		}
	}

	@PostMapping("/addcard")
	public AddCardResponse addCard(
			@Valid @RequestBody AddCardRequest addCardRequest,
			LithiumTokenUtil token,
			HttpServletRequest request) throws Status500InternalServerErrorException
	{
		try {
			List<DomainMethodProcessorProperty> properties = cashier.propertiesOfFirstEnabledProcessor(
					"checkout-cc", true,
					token.guid(), token.domainName(),
					request.getRemoteAddr(), request.getHeader("User-Agent"));

			String secretKey = null;
			String publicKey = null;
			Boolean useSandbox = false;

			for (DomainMethodProcessorProperty property : properties) {
				if (property.getProcessorProperty().getName().equalsIgnoreCase("secret_key")) {
					secretKey = property.getValue();
				}

				if (property.getProcessorProperty().getName().equalsIgnoreCase("public_key")) {
					publicKey = property.getValue();
				}

				if (property.getProcessorProperty().getName().equalsIgnoreCase("use_sendbox")) {
					useSandbox = Boolean.parseBoolean(property.getValue());
				}
			}

			UserApiInternalClient userClient = serviceFactory.target(UserApiInternalClient.class);
			Response<lithium.service.user.client.objects.User> userResponce = userClient.getUser(token.guid());
			lithium.service.user.client.objects.User user = null;

			if (userResponce.isSuccessful() && userResponce.getData() != null) {
				user =  userResponce.getData();
			} else {
				throw new DoErrorException("Unable to retrieve user from user service " + userResponce.toString());
			}

			TokenSource tokenSource = new TokenSource(addCardRequest.getToken());

			CheckoutApi checkoutApi = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);

			PaymentRequest<TokenSource> paymentRequest = PaymentRequest.fromSource(tokenSource, cashier.getCurrency(token.domainName()), 0L ); //cents

			CustomerRequest customer = new CustomerRequest();
			customer.setEmail(user.getEmail());
			customer.setName(addCardRequest.getNameOnCard() != null && !addCardRequest.getNameOnCard().isEmpty() ? addCardRequest.getNameOnCard() : user.getFirstName() + " " + user.getLastName());
			paymentRequest.setCustomer(customer);

			ThreeDSRequest threeDSRequest = new ThreeDSRequest();
			threeDSRequest.setEnabled(true);
			paymentRequest.setThreeDS(threeDSRequest);

			String return_url = addCardRequest.getReturnUrl();

			paymentRequest.setSuccessUrl(return_url + "?status=success");
			paymentRequest.setFailureUrl(return_url + "?status=failed");
			paymentRequest.setReference(properties.get(0).getDomainMethodProcessor().getId().toString());
			paymentRequest.setMetadata(new HashMap<>());
			paymentRequest.getMetadata().put("userGuid", token.guid());

			PaymentResponse apiResponse = checkoutApi.paymentsClient().requestAsync(paymentRequest).get();

			if (apiResponse.isPending()) {
				log.info("Payment is in the pending state, 3D secure check will be initiated. Link: " + apiResponse.getPending().getRedirectLink().getHref());
				return AddCardResponse.builder()
						.status(apiResponse.getPending().getStatus())
						.iframeRedirect(true)
						.iframeUrl(apiResponse.getPending().getRedirectLink().getHref()).build();
			} else if (apiResponse.getPayment().isApproved()) {
				return AddCardResponse.builder()
						.status("success")
						.iframeRedirect(false)
						.build();
			} else  {
				return AddCardResponse.builder()
						.status("failed")
						.iframeRedirect(false)
						.build();
			}
		} catch (Exception e) {
			log.error("Unable to add card for user: " + token.guid() + ", paymentToken:" + addCardRequest.getToken() + ". " + e.getMessage(), e);
			throw new Status500InternalServerErrorException("Failed to add card for user.");
		}
	}
}
