package lithium.service.cashier.frontend.paysafegateway.controllers;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.frontend.paysafegateway.Configuration;
import lithium.service.cashier.frontend.paysafegateway.data.objects.Payment;
import lithium.service.cashier.processor.paysafegateway.data.Error;
import lithium.service.cashier.processor.paysafegateway.data.IframeError;
import lithium.service.cashier.processor.paysafegateway.data.TransactionToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
	@Autowired Configuration configuration;
	@Autowired LithiumConfigurationProperties lithiumProperties;
	@Autowired MessageSource messages;

	@GetMapping
	public ModelAndView payment(
		@RequestParam("env") String environment,
		@RequestParam("sak") String suTokenApiKey,
		@RequestParam("aid") long accountId,
		@RequestParam("3ds") boolean useThreeDSecure,
		@RequestParam("3dsv2") boolean useThreeDSecureVersion2,
		@RequestParam("amt") long amount,
		@RequestParam("cur") String currencyCode,
		@RequestParam("tranid") String tranId
	) {
		Payment payment = Payment.builder()
		.environment(environment)
		.apiKey(suTokenApiKey)
		.accountId(accountId)
		.useThreeDSecure(useThreeDSecure)
		.useThreeDSecureVersion2(useThreeDSecureVersion2)
		.amountCents(amount)
		.amountFormatted(new BigDecimal(amount).movePointLeft(2).toPlainString())
		.currencyCode(currencyCode)
		.transactionId(tranId)
		.build();
		log.debug("Payment [payment="+payment+"]");
		return new ModelAndView("payment", "payment", payment);
	}

	@PostMapping("/paymentpost")
	private ModelAndView paymentPost(
		@RequestParam(name = "tranid") String tranId,
		@RequestParam(name = "token", required = false) String token,
		@RequestParam(name = "code", required = false) String code,
		@RequestParam(name = "correlationId", required = false) String correlationId,
		@RequestParam(name = "detailedMessage", required = false) String detailedMessage,
		@RequestParam(name = "displayMessage", required = false) String displayMessage,
		@RequestParam(name = "message", required = false) String message,
		@RequestHeader(value = "User-Agent") String userAgent,
		HttpServletRequest request
	) {
		IframeError iframeError=new IframeError(code,correlationId,detailedMessage,displayMessage,message);
		log.debug("Error Reponse: " + iframeError);
		log.debug("Payment post [tranId="+tranId+", userAgent="+userAgent+", userIp="+request.getRemoteAddr()+", token="+token+"]");
		TransactionToken tranToken = TransactionToken.builder()
				.transactionId(tranId).token(token).userAgent(userAgent).userIp(request.getRemoteAddr())
				.iframeError(iframeError)
				.build();
		RestTemplate rest = new RestTemplate();
		rest.postForObject(lithiumProperties.getGatewayPublicUrl() + configuration.getTokenPostUrl(), tranToken, String.class);
		return new ModelAndView("paymentprocessing");
	}
}
