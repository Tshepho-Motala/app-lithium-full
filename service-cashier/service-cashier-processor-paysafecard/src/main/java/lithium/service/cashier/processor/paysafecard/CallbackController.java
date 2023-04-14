package lithium.service.cashier.processor.paysafecard;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.skrill.data.enums.Status;
import lithium.service.cashier.processor.skrill.util.HashCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;

	@PostMapping(value="/a580673d7304629dbe8a729d02079d38/", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String handleCallback(
		HttpServletRequest webRequest,
		HttpServletResponse webResponse,
		@RequestParam("pay_to_email") String payToEmail,
		@RequestParam("pay_from_email") String payFromEmail,
		@RequestParam(value="merchant_id", required=false) String merchantId,
		@RequestParam(value="customer_id", required=false) String customerId,
		@RequestParam(value="transaction_id", required=false) String transactionId,
		@RequestParam("mb_transaction_id") String mbTransactionId,
		@RequestParam("mb_amount") String mbAmount,
		@RequestParam("mb_currency") String mbCurrency,
		@RequestParam("status") String status,
		@RequestParam(value="failed_reason_code", required=false) String failedReasonCode,
		@RequestParam("md5sig") String md5sig,
		@RequestParam(value="sha2sig", required=false) String sha2sig,
		@RequestParam("amount") String amount,
		@RequestParam("currency") String currency,
		@RequestParam(value="neteller_id", required=false) String netellerId,
		@RequestParam(value="payment_type", required=false) String paymentType,
		@RequestParam(value="merchant_fields", required=false) String merchantFields
	) throws Exception {
		String parameters = Collections.list(webRequest.getParameterNames())
		.stream()
		.map(p -> p + ": " + Arrays.asList(webRequest.getParameterValues(p)))
		.collect(Collectors.joining(", "));
		log.info("Callback received from Paysafecard:: " + parameters);
		Long internalTranId = Long.parseLong(transactionId);
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "paysafecard");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		HashCalculator hashCalc = new HashCalculator();
		hashCalc.addItem("merchant_id", merchantId);
		hashCalc.addItem("transaction_id", transactionId);
		hashCalc.addItem("secret_word", hashSecretWord(processorRequestResponse.getData().getProperty("secretWord")));
		hashCalc.addItem("mb_amount", mbAmount);
		hashCalc.addItem("mb_currency", mbCurrency);
		hashCalc.addItem("status", status);
		String md5Hash = hashCalc.calculateHash().toUpperCase();
		if (!md5Hash.contentEquals(md5sig)) {
			log.error("MD5 signature mismatch! Calculated hash: " + md5Hash + ", md5Sig received: " + md5sig);
			throw new Exception("MD5 signature mismatch!");
		}
		DoProcessorResponseStatus doProcessorResponseStatus = null;
		Status responseStatus = Status.fromCode(status);
		switch (responseStatus) {
			case PENDING:
				doProcessorResponseStatus = DoProcessorResponseStatus.NEXTSTAGE;
				break;
			case CANCELLED:
			case FAILED:
				doProcessorResponseStatus = DoProcessorResponseStatus.DECLINED;
				break;
			case PROCESSED:
				doProcessorResponseStatus = DoProcessorResponseStatus.SUCCESS;
				break;
			case CHARGEBACK:
				doProcessorResponseStatus = DoProcessorResponseStatus.REVERSAL_NEXTSTAGE;
				break;
			default:
				doProcessorResponseStatus = DoProcessorResponseStatus.NOOP;
				break;
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
		.transactionId(doProcessorRequest.getTransactionId())
		.processorReference(mbTransactionId)
		.status(doProcessorResponseStatus)
		.rawResponseLog(parameters)
		.build();
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		return "OK";
	}

	private String hashSecretWord(String secretWord) {
		HashCalculator hashCalc = new HashCalculator();
		hashCalc.addItem("secret_word", secretWord);
		return hashCalc.calculateHash().toUpperCase();
	}
}
