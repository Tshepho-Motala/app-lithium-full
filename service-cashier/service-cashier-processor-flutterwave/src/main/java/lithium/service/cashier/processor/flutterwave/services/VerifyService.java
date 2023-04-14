package lithium.service.cashier.processor.flutterwave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.enums.CashierPaymentType;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveCard;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesCustomer;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveGetTransactionResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveGetTransactionResponseData;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveVerifyResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveVerifyResponseData;
import lithium.service.cashier.processor.flutterwave.exceptions.Status500VerifyException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Service
@Slf4j
public class VerifyService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	public DoProcessorResponseStatus verify(DoProcessorRequest request, DoProcessorResponse response) throws Exception {
		DoProcessorResponseStatus status = null;
		//no point to call flutterwave in case processor reference is not specified
		if (request.getProcessorReference() == null)
			return DoProcessorResponseStatus.NOOP;

		FlutterWaveVerifyResponse flutterWaveVerifyResponse = callFlutterWaveVerify(request, response);
		response.addRawResponseLog("Received validation response: " + objectToPrettyString(flutterWaveVerifyResponse));
		FlutterWaveVerifyResponseData flutterWaveVerifyResponseData = flutterWaveVerifyResponse.getData();

		if (!flutterWaveVerifyResponse.getStatus().equalsIgnoreCase("success")) {
			String responseStatus;
			String message;
			if (flutterWaveVerifyResponseData.getStatus()!=null) {
				responseStatus = flutterWaveVerifyResponseData.getStatus();
				message = flutterWaveVerifyResponseData.getProcessorResponse();
			} else {
				responseStatus = flutterWaveVerifyResponse.getStatus();
				message = flutterWaveVerifyResponse.getMessage();
			}
			String declineReason = "(" + responseStatus + ") " + message;
			response.setDeclineReason(declineReason);
			log.info(declineReason + " .TransactionId=" + request.getTransactionId());
			return DoProcessorResponseStatus.DECLINED;
		}

		if (flutterWaveVerifyResponseData.getStatus().equalsIgnoreCase("successful")) {
			response.setAmountCentsReceived(CurrencyAmount.fromAmount(flutterWaveVerifyResponseData.getAmount()).toCents().intValue());
			status = DoProcessorResponseStatus.SUCCESS;
		} else if (flutterWaveVerifyResponseData.getStatus().equalsIgnoreCase("failed")) {
			String declineReason = "(" + flutterWaveVerifyResponseData.getStatus() + ") " + flutterWaveVerifyResponseData.getProcessorResponse();
			response.setDeclineReason(declineReason);
			log.info(declineReason + " .TransactionId=" + request.getTransactionId());
			status = DoProcessorResponseStatus.DECLINED;
		} else if (flutterWaveVerifyResponseData.getStatus().equalsIgnoreCase("pending")) {
			status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        }

        CashierPaymentType paymentType = CashierPaymentType.fromDescription(flutterWaveVerifyResponseData.getPaymentType());
        if (paymentType != null) {
            response.setPaymentType(paymentType.toString().toLowerCase());
        }
        String flwRef = flutterWaveVerifyResponseData.getFlwRef();
        if (flwRef!=null && !flwRef.isEmpty()) {
            response.setAdditionalReference(flwRef);
        }

        response.setOutputData(2, "processor_response", flutterWaveVerifyResponseData.getProcessorResponse());

		if (status.equals(DoProcessorResponseStatus.SUCCESS) && paymentType != null)
			response.setProcessorAccount(createDepositProcessorAccount(paymentType, flutterWaveVerifyResponseData, request.getUser()));

        return status;
    }

	private FlutterWaveVerifyResponse callFlutterWaveVerify(DoProcessorRequest request, DoProcessorResponse response) throws Status500VerifyException {
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Authorization", request.getProperty("secret_key"));
            HttpEntity<?> entity = new HttpEntity<>(headers);

            Map<String, String> map = new HashMap<>();
            map.put("id", request.getProcessorReference());

			response.addRawRequestLog("Verify deposit request: " + httpEntityToPrettyString(entity));
			log.info("FlutterWave verify deposit request (" + request.getTransactionId() + "): " + entity);

			ResponseEntity<String> verifyResponseEntity = restTemplate.exchange(request.getProperty("verify_api_url"),
				            HttpMethod.GET, entity,
				            String.class, map);

			response.addRawResponseLog("Verify deposit response: " + jsonObjectToPrettyString(verifyResponseEntity.getBody()));

	        FlutterWaveVerifyResponse verifyResponse =
			        mapper.readValue(verifyResponseEntity.getBody(), FlutterWaveVerifyResponse.class);

			if (!verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
				log.error("FlutterWave verify deposit failed (" + request.getTransactionId() + ") (" + verifyResponseEntity.getStatusCodeValue() + "): " + verifyResponseEntity.getBody());
				throw new Exception("FlutterWave verify deposit failed (" + verifyResponseEntity.getStatusCodeValue() + ") " + verifyResponse.getMessage());
			}
			log.info("FlutterWave verify response (" +request.getTransactionId() + "): " + verifyResponseEntity);

	        return verifyResponse;
        } catch (Exception e) {
	        throw new Status500VerifyException("Verify error (" + request.getTransactionId() + "): " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

	public DoProcessorResponseStatus getTransfer(DoProcessorRequest request, DoProcessorResponse response) throws Exception {

		if (request.getProcessorReference() == null) {
			response.addRawResponseLog("Cant get FlutterWave transfer : reference is null");
			log.error("Cant get FlutterWave withdraw transfer=" + request.getTransactionId() + " : reference is null");
			return DoProcessorResponseStatus.NOOP;
		}

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Authorization", request.getProperty("secret_key"));
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, String> map = new HashMap<>();

		ResponseEntity<String> getTransferResponseEntity =
				restTemplate.exchange(request.getProperty("get_transfer_api_url") + request.getProcessorReference(),
						HttpMethod.GET, entity,
						String.class, map);

		response.addRawResponseLog("Get transfer response: " + httpEntityToPrettyString(getTransferResponseEntity));

		FlutterWaveGetTransactionResponse getTransferResponse =
				mapper.readValue(getTransferResponseEntity.getBody(), FlutterWaveGetTransactionResponse.class);

		if (!getTransferResponseEntity.getStatusCode().is2xxSuccessful()) {
			log.error("FlutterWave withdraw get transfer failed (" + request.getTransactionId() + ") (" + getTransferResponseEntity.getStatusCodeValue() + "): " + getTransferResponseEntity.getBody());
			String message = getTransferResponse.getMessage();
			if (nonNull(getTransferResponse.getData())) {
				message += getTransferResponse.getData().getComplete_message();
			}
			throw new Exception("FlutterWave withdraw get transfer failed (" + getTransferResponseEntity.getStatusCodeValue() + ") " + message);
		}

		log.info("Withdraw get transfer response (" + request.getTransactionId() + "): " + getTransferResponseEntity);

		if (!getTransferResponse.getStatus().equals("success")) {
			throw new Exception("Verify did not return success: " +
					getTransferResponse.getStatus() + " " + getTransferResponse.getMessage());
		}

		FlutterWaveGetTransactionResponseData transferResponseData = getTransferResponse.getData();

		String transferStatus = transferResponseData.getStatus().toUpperCase();

		switch (transferStatus) {
			case "SUCCESSFUL": {
				response.setAmountCentsReceived(CurrencyAmount.fromAmount(transferResponseData.getAmount()).toCents().intValue());
				return DoProcessorResponseStatus.SUCCESS;
			}
			case "FAILED": {
				String declineReason = "(" + transferStatus + ") " + transferResponseData.getComplete_message();
				response.setDeclineReason(declineReason);
				log.info(declineReason + " .TransactionId=" + request.getTransactionId());
				return DoProcessorResponseStatus.DECLINED;
			}
		}
		return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
	}

	private ProcessorAccount createWithdrawProcessorAccount(FlutterWaveGetTransactionResponseData transferResponseData, DoProcessorRequestUser requestUser) {
		String bankDataDescriptor = new StringBuilder().append(transferResponseData.getBank_code()).append("/").append(transferResponseData.getAccount_number()).toString();
		return ProcessorAccount.builder()
				.reference(requestUser.getGuid() + "/" + bankDataDescriptor)
				.status(PaymentMethodStatusType.ACTIVE)
				.type(ProcessorAccountType.BANK)
				.descriptor(bankDataDescriptor)
				.name(transferResponseData.getFull_name())
				.data(new HashMap<String, String>() {{
					put("account_number", transferResponseData.getAccount_number());
					put("bank_code", transferResponseData.getBank_code());
				}})
				.hideInDeposit(true)
				.build();
	}

	private ProcessorAccount createDepositProcessorAccount(CashierPaymentType paymentType, FlutterWaveVerifyResponseData flutterWaveVerifyResponseData, DoProcessorRequestUser requestUser) {
		try {
			FlutterWaveChargesCustomer customer = flutterWaveVerifyResponseData.getCustomer();
			if (paymentType.equals(CashierPaymentType.CARD)) {
				FlutterWaveCard card = flutterWaveVerifyResponseData.getCard();
				return ProcessorAccount.builder()
						.reference(flutterWaveVerifyResponseData.getAccountId() + "*" + card.getLast4Digits())
						.status(PaymentMethodStatusType.HISTORIC)
						.hideInDeposit(false)
						.type(ProcessorAccountType.CARD)
						.name(customer.getName())
						.descriptor(card.getLast4Digits())
						.data(new HashMap<String, String>() {{
							put("expiry", card.getExpiry());
							put("last_4digits", card.getLast4Digits());
							put("first_6digits", card.getFirst6Digits());
							put("country", card.getCountry());
							put("type", card.getType());
							put("issuer", card.getIssuer());
						}}).build();
			}
		} catch (Exception ex) {
			log.error("Cant parse Flutterwave verify response: " + flutterWaveVerifyResponseData, ex);
		}
		return null;
	}
}
