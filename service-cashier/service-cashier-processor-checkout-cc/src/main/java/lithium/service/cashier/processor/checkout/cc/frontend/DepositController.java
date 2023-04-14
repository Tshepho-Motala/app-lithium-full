package lithium.service.cashier.processor.checkout.cc.frontend;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.frontend.model.Payment;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/frontend/deposit")
@Slf4j
public class DepositController {
	@Autowired
	LithiumConfigurationProperties lithiumProperties;

	@Autowired
	CashierInternalClientService cashier;

	@Autowired
	@Qualifier("lithium.service.cashier.RestTemplate")
	private RestTemplate restTemplate;

	@GetMapping
	public ModelAndView deposit(LithiumTokenUtil token, @RequestParam(required = false) Boolean newCard, HttpServletRequest request) throws Exception {
		String publicKey = cashier.propertyOfFirstEnabledProcessor("public_key",
				"checkout-cc", true, token.guid(), token.domainName(),
				request.getRemoteAddr(), request.getHeader("User-Agent")).getValue();

		List<UserCard> userCards = BooleanUtils.isTrue(newCard) ? Collections.emptyList() : cashier.getUserCards("checkout-cc", true,
				token.username(), token.domainName(), token.guid(),
				request.getRemoteAddr(), request.getHeader("User-Agent"))
				.stream().filter(uC -> uC.getLastFourDigits() != null)
				.collect(Collectors.toList());

		Payment deposit = Payment.builder()
				.publicKey(publicKey)
				.userCards(userCards)
				.userFullName(token.firstName() + " " + token.lastName())
				.build();
		log.debug("Deposit [deposit=" + deposit + "]");
		return new ModelAndView("deposit", "deposit", deposit);
	}

	@PostMapping("/do")
	private RedirectView doCard(
			@RequestParam(name = "amount") String amount,
			@RequestParam(name = "token", required = false) String token,
			@RequestParam(name = "used_card", required = false) String cardReference,
			@RequestParam(name = "nameoncard", required = false) String nameOnCard,
			@RequestParam(name = "cvv", required = false) String cvv,
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil litiumToken,
			HttpServletRequest request) throws Exception
	{
		log.debug("Payment post [amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + ", cardReference=" + cardReference + "]");
		try {

			DoRequest depositRequest = DoRequest.builder()
					.stage(1)
					.state("VALIDATEINPUT").build();

			DoStateField ammountField = DoStateField.builder().value(amount).build();
			DoStateFieldGroup commonStateFields = new DoStateFieldGroup();
			commonStateFields.getFields().put("amount", ammountField);
			depositRequest.getInputFieldGroups().put("1", commonStateFields);

			DoStateFieldGroup stateFields = new DoStateFieldGroup();
			DoStateField cardRefField;
			if (token != null && !token.isEmpty()) {
				cardRefField = DoStateField.builder().value(token).build();
				stateFields.getFields().put("paymentToken", cardRefField);
			} else {
				cardRefField = DoStateField.builder().value(cardReference).build();
				stateFields.getFields().put("cardReference", cardRefField);
				if (cvv != null && !cvv.isEmpty()) {
					DoStateField cvvField = DoStateField.builder().value(cvv).build();
					stateFields.getFields().put("cvv", cvvField);
				}
			}

			if (nameOnCard != null && !nameOnCard.isEmpty()) {
				DoStateField nameField = DoStateField.builder().value(nameOnCard).build();
				stateFields.getFields().put("nameoncard", nameField);
			}

			stateFields.getFields().put("save_card", DoStateField.builder().value("true").build());

			stateFields.getFields().put("set_default", DoStateField.builder().value("true").build());

			stateFields.getFields().put("return_url",
					DoStateField.builder().value(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/deposit/result").build());

			depositRequest.getInputFieldGroups().put("2", stateFields);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Authorization", request.getHeader("Authorization"));
			headers.add("User-Agent", userAgent);
			HttpEntity<DoRequest> entity = new HttpEntity<>(depositRequest, headers);

			HttpEntity<DoResponse> response = restTemplate.postForEntity(lithiumProperties.getGatewayPublicUrl() + "/service-cashier/frontend/deposit/v2?methodCode=checkout-cc",
					entity, DoResponse.class);

			DoResponse doResponse = response.getBody();
			if (doResponse != null && doResponse.getState().equals("WAITFORPROCESSOR")
					&& doResponse.getIframeUrl() != null && !doResponse.getIframeUrl().isEmpty()) {
				return new RedirectView(doResponse.getIframeUrl());
			} else {
				return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/deposit/result?status=" + doResponse.getState() + "&error=" + doResponse.getErrorMessage());
			}
		} catch (Exception ex) {
			log.error("Failed  to process deposit request. User: " + litiumToken.guid() + " [amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + ", cardReference=" + cardReference + "]", ex);
			return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/deposit/result?status=DECLINE");
		}
	}

	@RequestMapping("/result")
	public String result(
			@RequestParam(name = "status", required = true) String status,
			@RequestParam(name = "error", required = false) String error)
	{
		String result = "Status: " + status;
		if (!status.equalsIgnoreCase("success")) {
		result += error != null ?  " Error: " + error : " Error: Something went wrong. Please contact customer support.";
	}
		return result;
	}
}
