package lithium.service.cashier.processor.flutterwave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.enums.CashierPaymentType;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesData;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveStandardRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveStandardRequestCustomer;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveStandardRequestCustomizations;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveStandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Slf4j
@Service
public class DepositService {

	@Autowired
	LithiumConfigurationProperties config;
	@Autowired
	CashierDoCallbackService cashier;
	@Autowired
	VerifyService verifyService;
	@Autowired
	private ObjectMapper mapper;

	private String getEmail(DoProcessorRequest request) throws Exception {
		DoProcessorRequestUser user = request.getUser();
		String email = user.getEmail();
		if (email == null || email.trim().isEmpty()) {
			String dummyEmail = request.getProperty("dummy_email");
			if (dummyEmail != null && !dummyEmail.trim().isEmpty()) {
				email = user.getCellphoneNumber().trim() + dummyEmail;
			}
		}
		return email;
	}

	public DoProcessorResponseStatus InitiateWebDeposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		FlutterWaveStandardRequest flutterWaveStandardRequest = FlutterWaveStandardRequest.builder()
				.tx_ref(request.getTransactionId().toString())
				.amount(request.processorCommunicationAmount().toString())
				.currency(request.getUser().getCurrency())
				.payment_options("bank,ussd")
				.redirect_url(config.getGatewayPublicUrl() + "/service-cashier-processor-flutterwave/public/redirectreturn")
                .customer(FlutterWaveStandardRequestCustomer.builder()
                        .email(getEmail(request))
                        .phonenumber(request.getUser().getCellphoneNumber())
                        .name(request.getUser().getFullName()).build())
                .customizations(FlutterWaveStandardRequestCustomizations.builder()
                        .title(request.getProperty("deposit_page_title"))
                        .logo(request.getProperty("deposit_page_logo_url"))
                        .description(request.getProperty("deposit_page_description")).build()
                ).build();

		String email = flutterWaveStandardRequest.getCustomer().getEmail();
		if (email == null || email.trim().length() == 0) {
			log.error("Empty Email. Cant process Flutterwave deposit (" + request.getTransactionId() + ") user=" + request.getUser().getGuid() + ", amount=" + request.processorCommunicationAmount().toString());
			response.setDeclineReason("Empty email, transaction declined");
			response.addRawRequestLog("Initial web deposit request: " + objectToPrettyString(flutterWaveStandardRequest));
			response.addRawResponseLog("Empty email, transaction declined");
			return DoProcessorResponseStatus.DECLINED;
		}

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", request.getProperty("secret_key"));
		HttpEntity<FlutterWaveStandardRequest> entity = new HttpEntity<>(flutterWaveStandardRequest, headers);

		log.info("FlutterWave initial web deposit request (" + request.getTransactionId() + "): " + entity);
		response.addRawRequestLog("Initial web deposit request: " + objectToPrettyString(entity));

		ResponseEntity<String>  fwStandardResponse = rest.exchange(request.getProperty("deposit_api_url"), HttpMethod.POST, entity, String.class);

		response.addRawResponseLog("Initial web deposit response: " + jsonObjectToPrettyString(fwStandardResponse.getBody()));
		FlutterWaveStandardResponse flutterWaveStandardResponse = mapper.readValue(fwStandardResponse.getBody(), FlutterWaveStandardResponse.class);

		if (!fwStandardResponse.getStatusCode().is2xxSuccessful()) {
			log.error("FlutterWave initial web deposit failed (" + request.getTransactionId() + ") (" + fwStandardResponse.getStatusCodeValue() + "): " + fwStandardResponse.getBody());
			throw new Exception("(" + fwStandardResponse.getStatusCodeValue() + ") " + flutterWaveStandardResponse.getMessage());
		}

		log.info("FlutterWave initial web deposit response (" + request.getTransactionId() + "): " + fwStandardResponse.getBody());

		response.setIframeUrl(flutterWaveStandardResponse.getData().getLink());

		return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
	}

    public DoProcessorResponseStatus InitiateUssdDeposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
        {
            FlutterWaveChargesRequest flutterWaveStandardRequest = FlutterWaveChargesRequest.builder()
                    .tx_ref(request.getTransactionId().toString())
                    .amount(request.processorCommunicationAmount().toString())
                    .account_bank(request.stageInputData(1, "bank_code"))
                    .currency(request.getUser().getCurrency())
                    .fullname(request.getUser().getFullName())
		            .phone_number(request.getUser().getTelephoneNumber())
		            .email(getEmail(request))
		            .build();
	        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	        headers.add("Authorization", request.getProperty("secret_key"));
	        HttpEntity<FlutterWaveChargesRequest> entity = new HttpEntity<>(flutterWaveStandardRequest, headers);
			response.addRawRequestLog("Initiate ussd deposit request: " + objectToPrettyString(entity));
			log.info("FlutterWave initial ussd deposit request (" + request.getTransactionId() + "): " + entity);

			ResponseEntity<String> fwResponse = rest.exchange(request.getProperty("charges_api_url"), HttpMethod.POST, entity, String.class);

			response.addRawResponseLog("Initiate ussd deposit response: " + jsonObjectToPrettyString(fwResponse.getBody()));
			FlutterWaveChargesResponse flutterWaveChargesResponse = mapper.readValue(fwResponse.getBody(), FlutterWaveChargesResponse.class);

			if (!fwResponse.getStatusCode().is2xxSuccessful()) {
				log.error("FlutterWave initial ussd deposit failed (" + request.getTransactionId() + ") (" + fwResponse.getStatusCodeValue() + "): " + fwResponse.getBody());
				String message = flutterWaveChargesResponse.getMessage();
				if (nonNull(flutterWaveChargesResponse.getData())) {
					message += ". " + flutterWaveChargesResponse.getData().getProcessor_response();
				}
				throw new Exception("(" + fwResponse.getStatusCodeValue() + ") " + message);
			}

			log.info("FlutterWave initial ussd deposit response (" + request.getTransactionId() + "): " + fwResponse.getBody());

	        if (flutterWaveChargesResponse.getStatus().equals("success")) {
		        response.setOutputData(1, "ussd", flutterWaveChargesResponse.getMeta().getAuthorization().getNote());
		        response.setPaymentType(CashierPaymentType.USSD.toString().toLowerCase());
		        response.setProcessorReference(flutterWaveChargesResponse.getData().getId().toString());
		        return DoProcessorResponseStatus.NEXTSTAGE;
	        } else {
		        FlutterWaveChargesData chargesData = flutterWaveChargesResponse.getData();
		        String status;
		        String message;
	        	if (chargesData!=null && chargesData.getStatus()!=null) {
			        status = chargesData.getStatus();
			        message = chargesData.getProcessor_response();
		        } else {
					status = flutterWaveChargesResponse.getStatus();
			        message = flutterWaveChargesResponse.getMessage();
		        }

				response.setDeclineReason("(" + status + ") " + message);
				log.info("FlutterWave initial ussd deposit failed (" + request.getTransactionId() +") (" + status + "): " + message);
                return DoProcessorResponseStatus.DECLINED;
            }
        }
    }

	public String processCancelRedirect(String status, Long cashierTransactionId) throws Exception
    {
        DoProcessorRequest request = cashier.getTransaction(cashierTransactionId, "flutterwave");
	    //transaction status will not be changed PLAT-2081
        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(cashierTransactionId)
                .rawResponseLog("Received redirect return. Status: " + status + " TransactionId= " + cashierTransactionId)
                .build();

        response.setOutputData(2, "processor_redirect_status", status);

        cashier.doSafeCallback(response);

        return request.stageInputData(1).get("return_url") + "?status=" + DoProcessorResponseStatus.PLAYER_CANCEL;
    }

    public String processRedirect(String status, Long transactionId, String  processorReference) throws Exception
    {
        DoProcessorRequest request = cashier.getTransaction(transactionId, "flutterwave");
        request.setProcessorReference(processorReference);

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(transactionId)
                .processorReference(processorReference)
                .rawRequestLog("Received redirect return: " + status + " " + transactionId + " " + processorReference)
				.status(DoProcessorResponseStatus.PENDING_AUTO_RETRY)
                .build();

        response.setOutputData(2, "processor_redirect_status", status);
		checkFinalizedAndStatus(request, response);
		cashier.doSafeCallback(response);

		try {
			DoProcessorResponseStatus responseStatus = verifyService.verify(request, response);
			//transaction status will not be changed on redirect
			return request.stageInputData(1).get("return_url") + "?status=" + responseStatus.toString() + "&reference=" + processorReference;
		} finally {
			cashier.doSafeCallback(response);
		}

    }

	public static void checkFinalizedAndStatus(DoProcessorRequest request, DoProcessorResponse response) {
		if (request.isTransactionFinalized() && nonNull(response.getStatus())) {
			log.warn("Transaction (" + request.getTransactionId() + ") already finalized and can't be change status to " + response.getStatus().name());
			response.addRawResponseLog("Transaction already finalized and can't be change status to " + response.getStatus().name());
			response.setStatus(null);
		}
	}

	public void processWebhook(FlutterWaveChargesData request) throws Exception {
        DoProcessorRequest cashierTransaction = cashier.getTransaction(Long.parseLong(request.getTx_ref()), "flutterwave");
        cashierTransaction.setProcessorReference(request.getId().toString());

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(Long.parseLong(request.getTx_ref()))
                .processorReference(request.getId().toString())
				.rawRequestLog("Received webhook call: " + objectToPrettyString(request))
				.status(DoProcessorResponseStatus.PENDING_AUTO_RETRY)
                .build();
		checkFinalizedAndStatus(cashierTransaction, response);
		cashier.doSafeCallback(response);

		try {
			DoProcessorResponseStatus status = verifyService.verify(cashierTransaction, response);
			response.setStatus(status);
		} finally {
			checkFinalizedAndStatus(cashierTransaction, response);
			cashier.doSafeCallback(response);
		}
    }
}
