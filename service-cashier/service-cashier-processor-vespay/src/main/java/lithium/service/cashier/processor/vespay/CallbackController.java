package lithium.service.cashier.processor.vespay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.vespay.data.enums.ShopCallbackErrorCode;
import lithium.service.cashier.processor.vespay.data.enums.ValidationCallbackErrorCode;
import lithium.service.cashier.processor.vespay.data.enums.VoucherCallbackErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;
	
	@RequestMapping("/130a21442bdc35f6bd53287775e10d49/")
	public String handleCallback(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, Exception {
		String sender = request.getParameter("sender");
		if (sender == null || sender.isEmpty()) {
			log.error("Callback from VESPay has no sender param!!!");
			return null;
		}
		switch (sender) {
			case "validation":
				return validationCallback(request.getParameter("sender"),
					request.getParameter("apikey"),
					(request.getParameter("statuscode") != null && !request.getParameter("statuscode").isEmpty())
						? Integer.parseInt(request.getParameter("statuscode")): null,
					request.getParameter("statusdescription"),
					request.getParameter("traceid"),
					(request.getParameter("transactionid") != null && !request.getParameter("transactionid").isEmpty())
						? Integer.parseInt(request.getParameter("transactionid")): null,
					request.getParameter("datetimecreated"),
					request.getParameter("timezone"),
					request, response);
			case "shop":
				return shopCallback(request.getParameter("sender"),
					request.getParameter("apikey"),
					(request.getParameter("statuscode") != null && !request.getParameter("statuscode").isEmpty())
						? Integer.parseInt(request.getParameter("statuscode")): null,
					request.getParameter("statusdescription"),
					request.getParameter("traceid"),
					(request.getParameter("transactionid") != null && !request.getParameter("transactionid").isEmpty())
						? Integer.parseInt(request.getParameter("transactionid")): null,
					(request.getParameter("orderid") != null && !request.getParameter("orderid").isEmpty())
						? Integer.parseInt(request.getParameter("orderid")): null,
					request.getParameter("billingdescriptor"),
					(request.getParameter("bin") != null && !request.getParameter("bin").isEmpty())
						? Integer.parseInt(request.getParameter("bin")): null,
					request.getParameter("datetimecreated"),
					request.getParameter("timezone"),
					request, response);
			case "voucher":
				return voucherCallback(request.getParameter("sender"),
					request.getParameter("apikey"),
					(request.getParameter("statuscode") != null && !request.getParameter("statuscode").isEmpty())
						? Integer.parseInt(request.getParameter("statuscode")): null,
					request.getParameter("statusdescription"),
					request.getParameter("traceid"),
					(request.getParameter("transactionid") != null && !request.getParameter("transactionid").isEmpty())
						? Integer.parseInt(request.getParameter("transactionid")): null,
					(request.getParameter("orderid") != null && !request.getParameter("orderid").isEmpty())
						? Integer.parseInt(request.getParameter("orderid")): null,
					request.getParameter("billingdescriptor"),
					(request.getParameter("bin") != null && !request.getParameter("bin").isEmpty())
						? Integer.parseInt(request.getParameter("bin")): null,
					request.getParameter("datetimecreated"),
					request.getParameter("timezone"),
					(request.getParameter("amount") != null && !request.getParameter("amount").isEmpty())
						? Integer.parseInt(request.getParameter("amount")): null,
					request.getParameter("currency"),
					request, response);
			default:
				log.error("Received invalid callback from VESPay!!! sender (" + sender + ")");
				return null;
		}
	}
	
	private String validationCallback(String sender, String apiKey, Integer statusCode, String statusDescription, String traceId, Integer transactionId,
			String dateTimeCreated, String timeZone, HttpServletRequest webRequest, HttpServletResponse webResponse) throws Exception {
		log.info("Received validation callback from VESPay (sender: " + sender + ", apiKey: " + apiKey + ", statusCode: " + statusCode
				+ ", statusDescription: " + statusDescription + ", traceId: " + traceId + ", transactionId: " + transactionId
				+ ", dateTimeCreated: " + dateTimeCreated + ", timeZone: " + timeZone + ")");
		
		Long internalTranId = Long.parseLong(traceId);
		
		DoProcessorResponseStatus status = null;
		
		ValidationCallbackErrorCode ec = ValidationCallbackErrorCode.find(statusCode);
		if (!ec.equals(ValidationCallbackErrorCode.I63) &&
			!ec.equals(ValidationCallbackErrorCode.I74) &&
			!ec.equals(ValidationCallbackErrorCode.I94)) {
				log.info("Declining transaction (" + internalTranId + ") due to (" + ec.getDescription() + ")");
				status = DoProcessorResponseStatus.DECLINED;
		} else {
			return "OK";
		}
		
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "vespay");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
		.transactionId(doProcessorRequest.getTransactionId())
		.processorReference((transactionId != null)? String.valueOf(transactionId): null)
		.status(status)
		.rawResponseLog(webRequest.getQueryString())
		.build();
		
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		return "OK";
	}
	
	private String shopCallback(String sender, String apiKey, Integer statusCode, String statusDescription, String traceId, Integer transactionId,
			Integer orderId, String billingDescriptor, Integer bin, String dateTimeCreated, String timeZone,
			HttpServletRequest webRequest, HttpServletResponse webResponse) throws Exception {
		log.info("Received shop callback from VESPay (sender: " + sender + ", apiKey: " + apiKey + ", statusCode: " + statusCode
				+ ", statusDescription: " + statusDescription + ", traceId: " + traceId + ", transactionId: " + transactionId
				+ ", orderId: " + orderId + ", billingDescriptor: " + billingDescriptor + ", bin: " + bin
				+ ", dateTimeCreated: " + dateTimeCreated + ", timeZone: " + timeZone + ")");
		
		Long internalTranId = Long.parseLong(traceId);
		
		DoProcessorResponseStatus status = null;
		
		ShopCallbackErrorCode ec = null;
		if (statusCode != null) ec = ShopCallbackErrorCode.find(statusCode);
		if ((ec == null || ec.equals(ShopCallbackErrorCode.I000)) && (orderId != null)) {
			status = DoProcessorResponseStatus.NEXTSTAGE;
		} else {
			if (!ec.equals(ShopCallbackErrorCode.I103) &&
				!ec.equals(ShopCallbackErrorCode.I204)) { // all others are error cases
					log.info("Declining transaction (" + internalTranId + ") due to (" + ec.getDescription() + ")");
					status = DoProcessorResponseStatus.DECLINED;
			} else {
				return "OK";
			}
		}
		
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "vespay");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();
		if (transactionId != null) output.put("processorReference", String.valueOf(transactionId));
		if (orderId != null) output.put("orderId", String.valueOf(orderId));
		if (billingDescriptor != null) output.put("billingDescriptor", billingDescriptor);
		outputData.put(2, output);
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
		.transactionId(doProcessorRequest.getTransactionId())
		.processorReference((transactionId != null)? String.valueOf(transactionId): null)
		.status(status)
		.outputData(outputData)
		.rawResponseLog(webRequest.getQueryString())
		.billingDescriptor(billingDescriptor)
		.build();
		
		if (status.equals(DoProcessorResponseStatus.NEXTSTAGE)) {
			log.info("Adding 60s sleep");
			// Additional time for the player to copy the voucher code before changing the stage and thereby changing the screen
			// which is presented to the user on the front-end to request the voucher code
			Thread.sleep(60000);
		}
		
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		if (status.equals(DoProcessorResponseStatus.NEXTSTAGE))
			webResponse.sendRedirect(config.getGatewayPublicUrl() + "/service-cashier/frontend/loadingrefresh");
		
		return "OK";
	}
	
	private String voucherCallback(String sender, String apiKey, Integer statusCode, String statusDescription, String traceId, Integer transactionId,
			Integer orderId, String billingDescriptor, Integer bin, String dateTimeCreated, String timeZone, Integer amount, String currency,
			HttpServletRequest webRequest, HttpServletResponse webResponse) throws Exception {
		log.info("Received voucher callback from VESPay (sender: " + sender + ", apikey: " + apiKey + ", statusCode: " + statusCode
				+ ", statusDescription: " + statusDescription + ", traceId: " + traceId + ", transactionId: " + transactionId
				+ ", orderId: " + orderId + ", billingDescriptor: " + billingDescriptor + ", bin: " + bin
				+ ", dateTimeCreated: " + dateTimeCreated + ", timeZone: " + timeZone + ", amount: " + amount + ", currency: " + currency + ")");
		
		Long internalTranId = Long.parseLong(traceId);
		
		DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
		
		VoucherCallbackErrorCode ec = null;
		if (statusCode != null) ec = VoucherCallbackErrorCode.find(statusCode);
		if (!ec.equals(VoucherCallbackErrorCode.I000)) {
			log.info("Declining transaction (" + internalTranId + ") due to (" + ec.getDescription() + ")");
			status = DoProcessorResponseStatus.DECLINED;
		}
		
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "vespay");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();
		if (transactionId != null) output.put("processorReference", String.valueOf(transactionId));
		if (orderId != null) output.put("orderId", String.valueOf(orderId));
		if (billingDescriptor != null) output.put("billingDescriptor", billingDescriptor);
		outputData.put(2, output);
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
		.transactionId(doProcessorRequest.getTransactionId())
		.processorReference((transactionId != null)? String.valueOf(transactionId): null)
		.status(status)
		.outputData(outputData)
		.rawResponseLog(webRequest.getQueryString())
		.build();
		
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		webResponse.sendRedirect(config.getGatewayPublicUrl() + "/service-cashier/frontend/loadingrefresh");
		return "OK";
	}
}
