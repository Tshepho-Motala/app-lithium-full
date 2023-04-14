package lithium.service.cashier.processor.checkout.cc.frontend;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.frontend.model.Payment;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/frontend/withdraw")
@Slf4j
public class WithdrawController {
	@Autowired
	CashierDoCallbackService service;
	@Autowired
	CashierInternalClientService cashier;

	@Autowired
	LithiumConfigurationProperties lithiumProperties;

	@Autowired
	@Qualifier("lithium.service.cashier.RestTemplate")
	private RestTemplate restTemplate;

	@GetMapping
	public ModelAndView withdraw(LithiumTokenUtil token, HttpServletRequest request) throws Exception {
		try {
			String publicKey = cashier.propertyOfFirstEnabledProcessor("public_key",
					"checkout-cc", false, token.guid(), token.domainName(),
					request.getRemoteAddr(), request.getHeader("User-Agent")).getValue();

			List<UserCard> userCards = cashier.getUserCards("checkout-cc", true,
					token.username(), token.domainName(), token.guid(),
					request.getRemoteAddr(), request.getHeader("User-Agent"))
					.stream().filter(uC -> uC.getLastFourDigits() != null)
					.collect(Collectors.toList());

			if (userCards == null || userCards.isEmpty()) {
				new ModelAndView("redirect: " + lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/withdraw/result?status=failed");
			}

			Payment withdraw = Payment.builder()
					.publicKey(publicKey)
					.userCards(userCards)
					.build();

			log.debug("Withdraw [withdraw=" + withdraw + "]");
			return new ModelAndView("withdraw", "withdraw", withdraw);
		} catch (Exception ex) {
			log.error("Failed  to process checkout withdraw request", ex);
			throw ex;
		}
	}

	@PostMapping("/do")
	private RedirectView paymentPost(
			@RequestParam(name = "amount") String amount,
			@RequestParam(name = "token", required = false) String token,
			@RequestParam(name = "used_card", required = false) String cardReference,
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil litiumToken,
			HttpServletRequest request) throws Exception
	{
		log.debug("Payment post [amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + ", cardReference=" + cardReference + "]");
		try {

			DoRequest withdrawRequest = DoRequest.builder()
					.stage(1)
					.state("VALIDATEINPUT").build();

			DoStateField ammountField = DoStateField.builder().value(amount).build();
			DoStateFieldGroup commonStateFields = new DoStateFieldGroup();
			commonStateFields.getFields().put("amount", ammountField);
			withdrawRequest.getInputFieldGroups().put("1", commonStateFields);

			DoStateFieldGroup stateFields = new DoStateFieldGroup();
			DoStateField cardRefField;
			if (token != null && !token.isEmpty()) {
				cardRefField = DoStateField.builder().value(token).build();
				stateFields.getFields().put("paymentToken", cardRefField);
			} else {
				cardRefField = DoStateField.builder().value(cardReference).build();
				stateFields.getFields().put("cardReference", cardRefField);
			}
			withdrawRequest.getInputFieldGroups().put("2", stateFields);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Authorization", request.getHeader("Authorization"));
			headers.add("User-Agent", userAgent);
			HttpEntity<DoRequest> entity = new HttpEntity<>(withdrawRequest, headers);

			HttpEntity<DoResponse> response = restTemplate.postForEntity(lithiumProperties.getGatewayPublicUrl() + "/service-cashier/frontend/withdraw/v2?methodCode=checkout-cc",
					 entity, DoResponse.class);

			DoResponse doResponse = response.getBody();
			if (doResponse != null && (doResponse.getState().equals("WAITFORAPPROVAL") || doResponse.getState().equals("ON_HOLD"))) {
				return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/withdraw/result?status=" + doResponse.getState());
			} else {
				return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/withdraw/result?status=" + doResponse.getState());
			}
		} catch (Exception ex) {
			log.error("Failed  to process withdraw request. User: " + litiumToken.guid() + " [amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + ", cardReference=" + cardReference + "]", ex);
			return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/withdraw/result?status=failed");
		}
	}

	@RequestMapping(value = "/result")
	public String result(
			@RequestParam(name = "status") String status)
	{
		return status;
	}
}
