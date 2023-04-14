package lithium.service.cashier.processor.btc.globalbitlocker;

import java.math.BigDecimal;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.btc.globalbitlocker.data.CallbackResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.enums.Status;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CallbackController {
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	private CashierDoCallbackService service;

	@PostMapping("/callback/Y2FsbGJhY2tmb3JzZXJ2aWNlLWNhc2hpZXItcHJvY2Vzc29yLWJ0Yy1ibG9ja2NoYWlu")
	public String callbackResponse(
		@RequestParam("h") String hash,
		@RequestParam("t") Long transactionId,
		@RequestParam("id") String id,
		@RequestParam("amount") String amount,
		@RequestParam("bitcoin_amount") String bitcoinAmount,
		@RequestParam("status") String processorStatus,
		HttpServletResponse webResponse
	) throws Exception {
		CallbackResponse callbackResponse = CallbackResponse.builder()
			.id(id)
			.amount(amount)
			.bitcoinAmount(bitcoinAmount)
			.status(processorStatus)
			.hash(hash)
			.transactionId(transactionId)
			.build();

		log.info("HttpServletResponse :"+webResponse);
		log.info("CallbackResponse :"+callbackResponse);
		
		Response<DoProcessorRequest> processorRequest = service.doCallbackGetTransaction(transactionId, "globalbitlockerbtc");
		if (!processorRequest.isSuccessful()) {
			throw new Exception(processorRequest.getMessage());
		}
		
//		"http://196.22.242.139:9000"
		String callbackUrl = config.getGatewayPublicUrl()+
			processorRequest.getData().getProperty("callback.url")+
			"?t="+transactionId;
		ReceiveAddressRequest checkSignature = ReceiveAddressRequest.builder()
			.apiKey(processorRequest.getData().getProperty("apikey"))
			.user(processorRequest.getData().getUser().getGuid())
			.callbackUrl(URLEncoder.encode(callbackUrl, "UTF-8"))
			.build();
		
		if (!checkSignature.calculateSign().equals(hash)) {
			throw new Exception("Invalid signature");
		}
		
		DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
		switch (Status.fromStatus(callbackResponse.getStatus())) {
			case COMPLETED:
				BigDecimal acceptedDifference = new BigDecimal("1");
				BigDecimal amountReceived = new BigDecimal(amount);
				BigDecimal amountEntered = new BigDecimal(processorRequest.getData().stageInputData(1, "amount"));
				if (((amountReceived.subtract(amountEntered)).add(acceptedDifference)).movePointRight(2).intValue() < 0) {
					status = DoProcessorResponseStatus.DECLINED;
				} else {
					status = DoProcessorResponseStatus.SUCCESS;
				}
				break;
			case EXPIRED:
				status = DoProcessorResponseStatus.DECLINED;
				break;
			case WAITING:
				status = DoProcessorResponseStatus.NOOP;
				break;
			default:
				status = DoProcessorResponseStatus.FATALERROR;
		}
		
		DoProcessorResponse processorResponse = DoProcessorResponse.builder()
				.transactionId(transactionId)
				.processorReference(id)
				.rawRequestLog(JsonStringify.objectToString(callbackResponse))
				.status(status)
				.amountCentsReceived(new BigDecimal(amount).multiply(new BigDecimal(100)).intValue())
				.build();

		log.info("Sending request to service-cashier: " + processorResponse.toString());

		Response<String> response = service.doCallback(processorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		return "*ok*";
	}
	
}
