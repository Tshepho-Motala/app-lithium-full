package lithium.service.cashier.processor.checkout.paypal;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/redirect")
@Slf4j
public class RedirectController{
	@Autowired
	LithiumConfigurationProperties lithiumProperties;
	@Autowired
	CashierDoCallbackService service;
	CashierInternalClientService cashier;

	@RequestMapping("/{transactionId}/success")
	public ModelAndView success(
			@PathVariable("transactionId") Long transactionId,
			@RequestParam(name = "cko-session-id", required = true) String sessionId) throws Exception  {
		try {
			log.debug("Received success redirect from checkout.com for transaction: " + transactionId + " session_id: " + sessionId);
			return new ModelAndView(processCheckoutRedirect(transactionId, sessionId));
		} catch (Exception ex) {
			log.error("Failed to process success redirect from checkout. TransactionId: " + transactionId + " sessionId: " + sessionId, ex);
			throw ex;
		}
	}

	@RequestMapping("/{transactionId}/failed")
	public ModelAndView failed(
			@PathVariable("transactionId") Long transactionId,
	        @RequestParam(name = "cko-session-id", required = false) String sessionId) throws Exception
	{
		try {
			log.debug("Received failed redirect from checkout.com for transaction: " + transactionId + " session_id: " + sessionId);

			return new ModelAndView(processCheckoutRedirect(transactionId, sessionId));

		} catch (Exception ex) {
			log.error("Failed to process failed redirect from checkout. TransactionId: " + transactionId, ex);
			throw ex;
		}
	}

	@RequestMapping("/result")
	public String result(
			@RequestParam(name = "status", required = true) String status)
	{
		return status;
	}

	private String processCheckoutRedirect(Long transactionId, String sessionId) throws Exception{
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(transactionId, "checkout-paypal");
		if (!processorRequestResponse.isSuccessful()) {
			log.error("Failed to get transactionid: " + transactionId);
			throw new Exception(processorRequestResponse.getMessage());
		}

		DoProcessorResponseStatus status = DoProcessorResponseStatus.NEXTSTAGE;
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();

		output.put("session_id", sessionId);

		outputData.put(1, output);
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
				.transactionId(transactionId)
				.status(status)
				.outputData(outputData)
				.build();
		log.debug("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<DoResponse> response = service.doSafeCallback(doProcessorResponse);
		if (!response.isSuccessful()) throw new Exception("Error code from payment provider: " + response.getStatus().id());
		log.debug("Received response from service-cashier: " + response.toString());

		DoResponse doResponse = response.getData();

		if (doResponse != null && doResponse.getState().equals("SUCCESS")) {
			return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=success";
		} else {
			return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=failed";
		}
	}
}
