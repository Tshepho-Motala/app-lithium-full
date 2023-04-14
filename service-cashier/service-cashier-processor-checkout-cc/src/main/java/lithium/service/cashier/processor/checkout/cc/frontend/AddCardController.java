package lithium.service.cashier.processor.checkout.cc.frontend;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.data.AddCardRequest;
import lithium.service.cashier.processor.checkout.cc.data.AddCardResponse;
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

@RestController
@RequestMapping("/public/frontend/addcard")
@Slf4j
public class AddCardController {
	@Autowired
	LithiumConfigurationProperties lithiumProperties;

	@Autowired
	CashierInternalClientService cashier;

	@Autowired
	@Qualifier("lithium.service.cashier.RestTemplate")
	private RestTemplate restTemplate;

	@GetMapping
	public ModelAndView addCard(LithiumTokenUtil token, HttpServletRequest request) throws Exception {
		String publicKey = cashier.propertyOfFirstEnabledProcessor("public_key",
				"checkout-cc", true, token.guid(), token.domainName(),
				request.getRemoteAddr(), request.getHeader("User-Agent")).getValue();

		Payment cardData = Payment.builder()
				.publicKey(publicKey)
				.userFullName(token.firstName() + " " + token.lastName())
				.build();
		log.debug("CardData [cardData=" + cardData + "]");
		return new ModelAndView("addcard", "cardData", cardData);
	}

	@PostMapping("/do")
	private RedirectView doCard(
			@RequestParam(name = "token") String token,
			@RequestParam(name = "nameoncard", required = false) String nameOnCard,
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil litiumToken,
			HttpServletRequest request) throws Exception
	{
		log.debug("Add card[userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + "]");
		try {

			AddCardRequest addCardRequest = AddCardRequest.builder()
					.token(token)
					.returnUrl(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/addcard/result")
					.nameOnCard(nameOnCard)
					.build();

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Authorization", request.getHeader("Authorization"));
			headers.add("User-Agent", userAgent);
			HttpEntity<AddCardRequest> entity = new HttpEntity<>(addCardRequest, headers);

			HttpEntity<AddCardResponse> response = restTemplate.postForEntity(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/addcard",
					entity, AddCardResponse.class);

			AddCardResponse addResponse = response.getBody();
			if (addResponse != null && addResponse.isIframeRedirect()) {
				return new RedirectView(addResponse.getIframeUrl());
			} else {
				return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/addcard/result?status=success");
			}
		} catch (Exception ex) {
			log.error("Failed  add payment card. User: " + litiumToken.guid() + " [userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + "]", ex);
			return new RedirectView(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public/frontend/addcard/result?status=failed");
		}
	}

	@RequestMapping("/result")
	public String result(
			@RequestParam(name = "status", required = true) String status,
			@RequestParam(name = "cko-session-id", required = false) String sessionId)
	{
		return status;
	}
}
