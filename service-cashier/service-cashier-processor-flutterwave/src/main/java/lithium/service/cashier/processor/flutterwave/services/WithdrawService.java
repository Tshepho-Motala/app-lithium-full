package lithium.service.cashier.processor.flutterwave.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.cashier.client.exceptions.Status439BankAccountLookupClientException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveBank;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveBanksResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWebhookRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWithdrawData;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWithdrawRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWithdrawResponse;
import lithium.service.cashier.processor.flutterwave.data.AccountVerificationResponse;
import lithium.service.cashier.processor.flutterwave.data.Bank;
import lithium.service.cashier.processor.flutterwave.data.DataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lithium.service.cashier.processor.flutterwave.services.DepositService.checkFinalizedAndStatus;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Slf4j
@Service
public class WithdrawService {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CashierDoCallbackService cashier;
	@Autowired
	private VerifyService verifyService;
	@Autowired
	private ObjectMapper mapper;

	private final String FAILED_STATUS = "FAILED";
	private final List<String> REASON_LIST = new ArrayList(
			Arrays.asList("Payout with this ref already exists"));

	public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {

		String bank_code;
		String account_number;

		ProcessorAccount processorAccount = request.getProcessorAccount();
		if (processorAccount!=null) {
			// is direct withdraw
			bank_code = processorAccount.getData().get("bank_code");
			account_number = processorAccount.getData().get("account_number");
		} else {
			bank_code = request.stageInputData(1, "bank_code");
			account_number = request.stageInputData(1, "account_number");
		}

		FlutterWaveWithdrawRequest flutterWaveWithdrawRequest = FlutterWaveWithdrawRequest.builder()
				.account_bank(bank_code)
				.account_number(account_number)
				.amount(request.inputAmount())
				.currency(request.getUser().getCurrency())
				.reference(buildRefferenceSuffix(request))
				.build();

		log.info("Initial withdraw request(" + request.getTransactionId() + "): " + flutterWaveWithdrawRequest);
		response.addRawRequestLog("Initial withdraw request: " + objectToPrettyString(flutterWaveWithdrawRequest));

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", request.getProperty("secret_key"));

		HttpEntity<FlutterWaveWithdrawRequest> entity = new HttpEntity<>(flutterWaveWithdrawRequest, headers);

		ResponseEntity<String> flwResponseObj =
				rest.exchange(request.getProperty("withdraw_api_url"), HttpMethod.POST, entity, String.class);

		response.addRawResponseLog("Initial withdraw response: " + httpEntityToPrettyString(flwResponseObj));

		if (!flwResponseObj.getStatusCode().is2xxSuccessful()) {
			return resolveUnSuccessResponse(flwResponseObj, request, response);
		}

		FlutterWaveWithdrawResponse flutterWaveWithdrawResponse = mapper.readValue(flwResponseObj.getBody(), FlutterWaveWithdrawResponse.class);

		log.info("Initial withdraw response (" + request.getTransactionId() + "): " + flwResponseObj);
		if (flutterWaveWithdrawResponse.getStatus().equalsIgnoreCase("success")) {
			response.setTransactionId(Long.valueOf(flutterWaveWithdrawResponse.getData().getId()));
			response.setProcessorReference(flutterWaveWithdrawResponse.getData().getId().toString());
			response.setAdditionalReference(flutterWaveWithdrawResponse.getData().getReference());
			return DoProcessorResponseStatus.NEXTSTAGE;
		} else {
			FlutterWaveWithdrawData flutterWaveWithdrawData = flutterWaveWithdrawResponse.getData();
			String declineReason = "(" + flutterWaveWithdrawData.getStatus() + ") " + flutterWaveWithdrawData.getComplete_message();
			response.setDeclineReason(declineReason);
			log.info(declineReason + " .TransactionId=" + request.getTransactionId());
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	private DoProcessorResponseStatus resolveUnSuccessResponse(ResponseEntity<String> flwResponseObj, DoProcessorRequest request, DoProcessorResponse response) {
		try {
			FlutterWaveWithdrawResponse flutterWaveWithdrawResponse = mapper.readValue(flwResponseObj.getBody(), FlutterWaveWithdrawResponse.class);
			if (flutterWaveWithdrawResponse.getData()!= null) {
				FlutterWaveWithdrawData data = flutterWaveWithdrawResponse.getData();
				if (data.getStatus().equalsIgnoreCase(FAILED_STATUS)) {
					response.setDeclineReason(data.getComplete_message());
					return DoProcessorResponseStatus.DECLINED;
				}
			} else {
				if (REASON_LIST.stream().filter(s -> s.equalsIgnoreCase(flutterWaveWithdrawResponse.getMessage())).count() > 0) {
					response.setDeclineReason(flutterWaveWithdrawResponse.getMessage());
					return DoProcessorResponseStatus.DECLINED;
				}
			}
			log.error("FlutterWave initial withdraw failed (" + request.getTransactionId() + ") (" + flwResponseObj.getStatusCodeValue() + "): " + flwResponseObj.getBody());
		} catch (Exception e) {
			log.error("FlutterWave initial withdraw failed (" + request.getTransactionId() + ") message : " + e.getMessage() , e);
		}
		return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
	}

    @Cacheable(value = "lithium.service.cashier.processor.flutterwave.banks",cacheManager = "banksCacheManager", key = "#country", unless = "#result == null")
    public List<Bank> getBankList(String country, String url, String secretKey) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", secretKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, String> map = new HashMap<>();
        map.put("country", country);

		ResponseEntity<String> banksResponseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET, entity,
                        String.class, map);

	    FlutterWaveBanksResponse fwResponse =
			    mapper.readValue(banksResponseEntity.getBody(), FlutterWaveBanksResponse.class);

		if (!banksResponseEntity.getStatusCode().is2xxSuccessful()) {
			log.error("FlutterWave getBankList failed (" + banksResponseEntity.getStatusCodeValue() + "): " + banksResponseEntity.getBody());
			throw new Exception("FlutterWave getBankList failed (" + banksResponseEntity.getStatusCodeValue() + ") " + fwResponse.getMessage());
		}

		log.info("Get banks list response: " + banksResponseEntity);

        if (!fwResponse.getStatus().equals("success")) {
            throw new Exception("Get banks list did not return success: " +
                    fwResponse.getStatus() + " " + fwResponse.getMessage());
        }

        List<FlutterWaveBank> fwBanks = fwResponse.getData();

        return fwBanks.stream()
                .map(fb -> Bank.builder()
                            .code(fb.getCode())
                            .name(fb.getName())
                            .build())
                .sorted()
                .collect(Collectors.toList());
    }

    public void proceedWithdrawWebhook(@RequestBody String data, FlutterWaveWebhookRequest request) throws Exception {

            JsonNode requestData = request.getData();

            DoProcessorResponseStatus status;

	        String reference = requestData.findValue("reference").textValue();
	        Long transactionId = buildTransactionId(reference);

	        DoProcessorRequest cashierTransaction = cashier.getTransaction(transactionId, "flutterwave");

	        DoProcessorResponse response = DoProcessorResponse.builder()
			        .transactionId(transactionId)
			        .rawRequestLog("Received webhook call: " + jsonObjectToPrettyString(data))
			        .build();
			cashier.doSafeCallback(response);

			if (cashierTransaction.getProcessorReference() == null) {
				String flwReference = requestData.findValue("id").textValue();
				String message = "Cant find flutterwave processor reference. Set data from webhook: " + flwReference;
				log.warn(message);
				response.setProcessorReference(flwReference);
				cashierTransaction.setProcessorReference(flwReference);
				response.setRawResponseLog(message);
			}

			try {
				if (requestData.findValue("status").textValue().equalsIgnoreCase("SUCCESSFUL")) {
					status = verifyService.getTransfer(cashierTransaction, response);
				} else {
					status = DoProcessorResponseStatus.DECLINED;
					log.error("Unsuccessful transaction(" + transactionId + ") details: " + requestData + " original data: " + data);
					String errorMessage = "(" + requestData.findValue("status").textValue() + ") " + requestData.findValue("complete_message").textValue();
					response.setDeclineReason(errorMessage);
				}
				response.setStatus(status);
			} finally {
				checkFinalizedAndStatus(cashierTransaction, response);
				cashier.doSafeCallback(response);
			}
    }

    private Long buildTransactionId(String reference) {
        String[] refParts = reference.split("_");
        if (refParts.length > 1) {
            return Long.valueOf(refParts[0]);
        }
        return Long.valueOf(reference);
    }

    private String buildRefferenceSuffix(DoProcessorRequest request) throws Exception {
        String transactionId = request.getTransactionId().toString();
        String suffix = request.getProperty("refference_suffix") ;
        if (suffix== null || suffix.trim().isEmpty()) {
            return transactionId;
        }
        return transactionId+suffix.trim();
    }

	public Response<BankAccountLookupResponse> bankAccountLookup(BankAccountLookupRequest bankAccountLookupRequest) throws Exception {
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		Map<String, String> domainMethodProcessorProperties = bankAccountLookupRequest.getDomainMethodProcessorProperties();
		try {
			if (bankAccountLookupRequest.getAccountNumber() == null) {
				throw new Status439BankAccountLookupClientException("Missing 'account_number' in withdraw request");
			}
			String accountNumber = bankAccountLookupRequest.getAccountNumber();
			if (bankAccountLookupRequest.getBankCode() == null) {
				throw new Status439BankAccountLookupClientException("Missing 'bank_code' in withdraw request");
			}
			String bankCode = bankAccountLookupRequest.getBankCode();
			String countryCode = domainMethodProcessorProperties.get("country_code");
			String banksUrl = domainMethodProcessorProperties.get("withdraw_banks_url");
			String secretKey = domainMethodProcessorProperties.get("secret_key");
			String bankName = getBankName(bankAccountLookupRequest, bankCode, countryCode, banksUrl, secretKey);
			AccountVerificationResponse accountVerificationResponse = getAccountVerificationResponse(secretKey, accountNumber, bankCode, domainMethodProcessorProperties);
			String accountName = accountVerificationResponse.getData().getAccountName();
			return Response.<BankAccountLookupResponse>builder().data(buildBankAccountLookupResponse(accountNumber, accountName, bankCode, bankName)).build();
		} catch (Status439BankAccountLookupClientException e) {
			log.error("Bank Account Lookup Failed: {}", e.getMessage());
			return Response.<BankAccountLookupResponse>builder().data(buildFailedBankAccountLookupResponse(e.getMessage())).build();
		}
	}

	private BankAccountLookupResponse buildFailedBankAccountLookupResponse(String failedStatusReasonMessage) {
		return BankAccountLookupResponse.builder().status("Failed").failedStatusReasonMessage(failedStatusReasonMessage).build();
	}

	private BankAccountLookupResponse buildBankAccountLookupResponse(String accountNumber, String accountName, String bankCode, String bankName) {
		return BankAccountLookupResponse.builder()
				.status("Success").accountNumber(accountNumber).accountName(accountName).bankCode(bankCode).bankName(bankName)
				.build();
	}

	private AccountVerificationResponse getAccountVerificationResponse(String secretKey, String accountNumber, String bankCode,
																	   Map<String, String> domainMethodProcessorProperties) throws Status439BankAccountLookupClientException, IOException {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", secretKey);
		DataRequest data = new DataRequest();
		data.setAccountNumber(accountNumber);
		data.setAccountBank(bankCode);
		HttpEntity<DataRequest> dataRequestHttpEntity = new HttpEntity<>(data, headers);
		ResponseEntity<String> responseEntity =
				restTemplate.exchange(domainMethodProcessorProperties.get("account_verification_url"), HttpMethod.POST, dataRequestHttpEntity, String.class);
		log.info("Validation response: " + responseEntity);
		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			throw new Status439BankAccountLookupClientException("Invalid input data. Please check your bank name, NUBAN details and try again.");
		}
		return mapper.readValue(responseEntity.getBody(), AccountVerificationResponse.class);
	}

	private String getBankName(BankAccountLookupRequest bankAccountLookupRequest, String bankCode, String countryCode, String banksUrl, String secretKey) throws Exception {
		return bankAccountLookupRequest.getBankName() != null
				? bankAccountLookupRequest.getBankName()
				: getBankList(countryCode, banksUrl, secretKey).stream()
				.filter(b -> b.getCode().equals(bankCode))
				.findFirst()
				.map(Bank::getName)
				.orElseThrow(() -> new Status439BankAccountLookupClientException("'bank_code' is invalid"));
	}
}
