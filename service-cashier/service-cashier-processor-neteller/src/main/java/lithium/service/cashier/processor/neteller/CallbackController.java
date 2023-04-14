package lithium.service.cashier.processor.neteller;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.neteller.data.WebHookEvent;
import lithium.service.cashier.processor.neteller.data.enums.WebHookEventType;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;

	@RequestMapping("/c7213c39687a9bcb30a2b366db1b1844/")
	public String handleCallback(
		HttpServletRequest webRequest,
		HttpServletResponse webResponse,
		@RequestBody WebHookEvent event
	) throws Exception {
		log.info("Callback received from Neteller:: " + event);
		Long internalTranId = Long.parseLong(event.getPayload().getMerchantRefNum());
		DoProcessorResponseStatus status = null;
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "neteller");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		WebHookEventType type = WebHookEventType.fromAction(event.getEventName());
		if (type.compareTo(WebHookEventType.PAYMENT_HANDLE_COMPLETED) == 0) {
			// Do nothing. Just acknowledge message from external processor.
			return "OK";
		}
		switch (type) {
			case PAYMENT_HANDLE_PAYABLE:
				status = DoProcessorResponseStatus.NEXTSTAGE;
				break;
			case PAYMENT_HANDLE_FAILED:
			case PAYMENT_FAILED:
			case SA_CREDIT_FAILED:
			case SA_CREDIT_CANCELLED:
				status = DoProcessorResponseStatus.DECLINED;
				break;
			case PAYMENT_COMPLETED:
			case SA_CREDIT_COMPLETED:
				status = DoProcessorResponseStatus.SUCCESS;
				break;
			default:
				status = DoProcessorResponseStatus.NOOP;
				break;
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
		.transactionId(doProcessorRequest.getTransactionId())
		.processorReference(event.getPayload().getId())
		.status(status)
		.rawResponseLog(JsonStringify.objectToString(event))
		.build();
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		return "OK";
	}
}
