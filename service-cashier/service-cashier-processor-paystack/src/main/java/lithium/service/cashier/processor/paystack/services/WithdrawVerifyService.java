package lithium.service.cashier.processor.paystack.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.processor.paystack.api.schema.RecipientDetails;
import lithium.service.cashier.processor.paystack.api.schema.VerifyResponse;
import lithium.service.cashier.processor.paystack.api.schema.VerifyResponseData;
import lithium.service.cashier.processor.paystack.exeptions.PaystackServiceHttpErrorException;
import lithium.service.cashier.processor.paystack.exeptions.PaystackTransactionNotFoundException;
import lithium.service.cashier.processor.paystack.exeptions.PaystackWrongConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

import static lithium.service.cashier.processor.paystack.services.WithdrawService.resolveDeclineReason;
import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.getPaystackMessageFromBody;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
@Service
@Slf4j
@EnableRestTemplate
public class WithdrawVerifyService extends BasePaystackService {

    private RestTemplate rest;
    private ObjectMapper mapper;

    @Autowired
    public WithdrawVerifyService(@Qualifier("lithium.rest") RestTemplateBuilder builder, ObjectMapper mapper) {
        this.rest = builder.build();
        this.mapper = mapper;
    }

    public DoProcessorResponseStatus verify(DoProcessorRequest request, DoProcessorResponse response) throws PaystackWrongConfigurationException, PaystackServiceHttpErrorException, IOException {
        DoProcessorResponseStatus status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        try {
            VerifyResponseData verifyResponseData = callPaystackVerify(request, response).getData();
            String responseStatus = verifyResponseData.getStatus().toLowerCase();

            switch (responseStatus) {
                case "success":
                    response.setAmountCentsReceived(verifyResponseData.getAmount().intValue());
                    status = DoProcessorResponseStatus.SUCCESS;
                    break;
                case "failed":
                    String declineReason = verifyResponseData.getReason();
                    log.error("Paystack transaction id=" + request.getTransactionId() + " failed. Message=" + declineReason);
                    response.setDeclineReason(resolveDeclineReason(declineReason));
                    status = DoProcessorResponseStatus.DECLINED;
                    break;
                case "pending":
                    status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;
                    break;
            }
            response.addRawResponseLog("Received status response: " + status);
            response.addRawResponseLog("Received validation response: " + objectToPrettyString(verifyResponseData));
            response.setStatus(status);
        } catch (PaystackTransactionNotFoundException e) {
            response.setDeclineReason("Transaction not found on Paystack side");
            status = DoProcessorResponseStatus.DECLINED;
            response.setStatus(status);
        }

        return status;
    }

    private VerifyResponse callPaystackVerify(DoProcessorRequest request, DoProcessorResponse response) throws PaystackWrongConfigurationException, PaystackTransactionNotFoundException, PaystackServiceHttpErrorException, IOException {

        MultiValueMap<String, String> headers = prepareHeaders(request);

        ResponseEntity<String> verifyResponseEntity =
                rest.exchange(property("paystack_verify_api_url", request) + request.getTransactionId(),
                        HttpMethod.GET, new HttpEntity<>(headers), String.class, new HashMap<>());

        response.addRawResponseLog("Verify response: " + httpEntityToPrettyString(verifyResponseEntity));

        if (!verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Paystack withdraw verify call failed (" + verifyResponseEntity.getStatusCodeValue() + ") " + verifyResponseEntity.getBody() + "(" + request.getTransactionId() + ")");
            String message = getPaystackMessageFromBody(mapper, verifyResponseEntity.getBody());
            if (message.contains("Transfer not found")) {
                throw new PaystackTransactionNotFoundException();
            }
            throw new PaystackServiceHttpErrorException(verifyResponseEntity.getBody(), verifyResponseEntity.getStatusCodeValue());
        }

	    log.info("Paystack withdraw verify response " + verifyResponseEntity + " " + request + " .TransactionId=" + request.getTransactionId());

        return mapper.readValue(verifyResponseEntity.getBody(), VerifyResponse.class);
    }

	private ProcessorAccount createProcessorAccount(VerifyResponseData verifyResponseData,  DoProcessorRequestUser requestUser) {
		RecipientDetails recipientDetails = verifyResponseData.getRecipient().getDetails();
		if (recipientDetails!=null) {

			String bankCode = recipientDetails.getBankCode();
			String accountNumber = recipientDetails.getAccountNumber();
			String bankDataDescriptor = new StringBuilder().append(bankCode).append("/").append(accountNumber).toString();

			return ProcessorAccount.builder()
					.reference(requestUser.getGuid()+"/"+bankDataDescriptor)
					.status(PaymentMethodStatusType.ACTIVE)
					.type(ProcessorAccountType.BANK)
					.descriptor(bankDataDescriptor)
					.name(recipientDetails.getAccountName())
					.data(new HashMap<String, String>() {{
						put("account_number", accountNumber);
						put("bank_code", bankCode);
					}})
					.hideInDeposit(true)
					.build();
		}
		return null;
	}

}
