package lithium.service.cashier.processor.paystack.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.enums.CashierPaymentType;
import lithium.service.cashier.processor.paystack.api.schema.AuthorizationData;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackVerificationResponse;
import lithium.service.cashier.processor.paystack.api.schema.deposit.VerificationResponseData;
import lithium.service.cashier.processor.paystack.exeptions.PaystackServiceHttpErrorException;
import lithium.service.cashier.processor.paystack.exeptions.Status500VerifyException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.getPaystackMessageFromBody;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;

@Service
@Slf4j
@EnableRestTemplate
public class DepositVerifyService {

    private RestTemplate rest;
    private ObjectMapper mapper;

    @Autowired
    public DepositVerifyService(@Qualifier("lithium.rest") RestTemplateBuilder builder, ObjectMapper mapper) {
        this.rest = builder.build();
        this.mapper = mapper;
    }

    @Autowired
    CashierInternalClientService cashierService;

    public DoProcessorResponseStatus verify(DoProcessorRequest request, DoProcessorResponse response, boolean isRedirect) throws Exception {
	    DoProcessorResponseStatus status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;

	    //no point to call paystack in case processor reference is not specified
	    if (request.getProcessorReference() == null)
		    return DoProcessorResponseStatus.NOOP;

	    PaystackVerificationResponse verificationResponse = callPaystackVerify(request, response);
	    VerificationResponseData verificationResponseData = verificationResponse.getData();
	    AuthorizationData authData = verificationResponseData.getAuthorization();

        if (verificationResponseData.getStatus().toLowerCase().equals("success")) {
            response.setAmountCentsReceived(verificationResponseData.getAmount().intValue());
            status = DoProcessorResponseStatus.SUCCESS;
            Boolean saveCard = request.stageInputData(1).get("save_card") != null ? Boolean.parseBoolean(request.stageInputData(1).get("save_card")) : false;
            if ("card".equalsIgnoreCase(authData.getChannel())) {
            if (request.stageInputData(1).get("cardReference") == null && saveCard && authData != null
                    && authData.getReusable() != null && authData.getReusable()) {
                    Long paymentMethodId = saveUserCard(authData, request.getTransactionId(), request.getProperty("disable_account_for_bins"));
                    response.setOutputData(2, "cardSourceId", authData.getSignature());
                    response.setPaymentMethodId(paymentMethodId);
                }
           } else if ("bank".equalsIgnoreCase(authData.getChannel())) {
                ProcessorAccount processorAccount = ProcessorAccount.builder()
                        .reference(verificationResponseData.getCustomer() == null ? authData.getLastFour() : verificationResponseData.getCustomer().getId() + "_" + authData.getLastFour())
                        .status(PaymentMethodStatusType.HISTORIC)
                        .type(ProcessorAccountType.BANK)
                        .name(authData.getAccountName())
                        .descriptor(authData.getLastFour())
                        .hideInDeposit(true)
                        .build();
                response.setProcessorAccount(processorAccount);
           }
        } else if (verificationResponseData.getStatus().toLowerCase().equals("failed")) {
	        String declineReason = verificationResponseData.getGatewayResponse()!=null ? verificationResponseData.getGatewayResponse() : verificationResponse.getMessage();
		    log.error("Paystack transaction id=" + request.getTransactionId() + " failed. Message=" + declineReason);
		    response.setDeclineReason(declineReason);
		    status = DoProcessorResponseStatus.DECLINED;
	    }

	    if (request.stageInputData(1).get("cardReference") == null && !request.stageOutputData(2).containsKey("remark_added")
			    && !isRedirect && authData != null && "card".equalsIgnoreCase(authData.getChannel())) {
		    if (addCardRemark(request.getTransactionId(), authData))
			    response.stageOutputData(2).put("remark_added", "true");
	    }

	    response.setStatus(status);

	    CashierPaymentType paymenttype = CashierPaymentType.fromDescription(verificationResponseData.getChannel());
	    if (paymenttype != null) {
		    response.setPaymentType(paymenttype.toString().toLowerCase());
	    }
	    return status;
    }

	public DoProcessorResponseStatus verifyUssdRequest(DoProcessorRequest request, DoProcessorResponse response) throws Exception {
		DoProcessorResponseStatus status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;
		PaystackVerificationResponse verificationResponse = callPaystackUSSDVerify(request, response);
		status = updateStatus(status, verificationResponse, request, response);
		return status;
	}

    private DoProcessorResponseStatus updateStatus(DoProcessorResponseStatus status, PaystackVerificationResponse verificationResponse, DoProcessorRequest request, DoProcessorResponse response) throws Exception {
	    VerificationResponseData verificationResponseData = verificationResponse.getData();
        switch (verificationResponseData.getStatus().toLowerCase()) {
            case "success":
                response.setAmountCentsReceived(verificationResponseData.getAmount().intValue());
                status = DoProcessorResponseStatus.SUCCESS;
                break;
            case "failed":
	            String declineReason = verificationResponseData.getGatewayResponse()!=null ? verificationResponseData.getGatewayResponse() : verificationResponse.getMessage();
	            log.error("Paystack transaction id=" + request.getTransactionId() + " failed. Message=" + declineReason);
	            response.setDeclineReason(declineReason);
	            status = DoProcessorResponseStatus.DECLINED;
	            break;
            case "abandoned":
                status = checkForExpired(verificationResponseData, request, status);
                break;
        }
        response.setStatus(status);
        response.addRawRequestLog("Received status response: " + status.toString());
        response.addRawResponseLog("Received validation response: " + objectToPrettyString(verificationResponseData));
        CashierPaymentType paymenttype = CashierPaymentType.fromDescription(verificationResponseData.getChannel());
        if (paymenttype != null) {
            response.setPaymentType(paymenttype.toString().toLowerCase());
        }
        return status;
    }

	private DoProcessorResponseStatus checkForExpired(VerificationResponseData verificationResponseData, DoProcessorRequest request, DoProcessorResponseStatus status) throws Exception {
		DateTimeZone timeZone = DateTimeZone.getDefault();
		DateTime now = DateTime.now(timeZone);
		DateTime startDate = DateTime.parse(verificationResponseData.getCreatedAt());
		if (!request.getProperty("ussd_expired_after_minutes").equalsIgnoreCase("0") &&
				startDate.plusMinutes(Integer.parseInt(request.getProperty("ussd_expired_after_minutes"))).isBefore(now.getMillis())) {
			status = DoProcessorResponseStatus.EXPIRED;
		}
		return status;
	}

	private PaystackVerificationResponse callPaystackVerify(DoProcessorRequest request, DoProcessorResponse response) throws Status500VerifyException {
		try {
			return doPaystackCall(request, response, request.getProcessorReference());
		} catch (Exception e) {
			log.error("Got deposit verify error (" + request.getTransactionId() + ", " + request.getProcessorReference() + "): " + ExceptionMessageUtil.allMessages(e));
			throw new Status500VerifyException("Verify error: " + ExceptionMessageUtil.allMessages(e), e);
		}
	}

	private PaystackVerificationResponse callPaystackUSSDVerify(DoProcessorRequest request, DoProcessorResponse response ) throws Status500VerifyException {
		try {

			PaystackVerificationResponse verifyResponse = doPaystackCall(request, response, request.getTransactionId().toString());

			if (!verifyResponse.isStatus()) {
				throw new Exception("Verify error " + verifyResponse.getMessage());
			}
			return verifyResponse;
		} catch (Exception e) {
			log.error("Got ussd deposit verify error (" + request.getTransactionId() + ", " + request.getProcessorReference() + "): " + ExceptionMessageUtil.allMessages(e));
			throw new Status500VerifyException("Verify error: " + ExceptionMessageUtil.allMessages(e), e);
		}
    }

    private PaystackVerificationResponse doPaystackCall(DoProcessorRequest request, DoProcessorResponse response, String id) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", "Bearer " + request.getProperty("secret_key"));
        headers.add("content-type", "application/json");
        headers.add("User-Agent", "Paystack-Developers-Hub");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, String> map = new HashMap<>();

        map.put("id", id);

        ResponseEntity<String> verifyResponseEntity =
                rest.exchange(request.getProperty("verify_deposit_url"), HttpMethod.GET, entity, String.class, map);

	    response.addRawResponseLog("Verify deposit response : " + httpEntityToPrettyString(verifyResponseEntity));
	    PaystackVerificationResponse paystackVerificationResponse = mapper.readValue(verifyResponseEntity.getBody(), PaystackVerificationResponse.class);

        if (!verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Paystack verify transaction id=" + request.getTransactionId() + " call failed (" + verifyResponseEntity.getStatusCodeValue() + ") " + verifyResponseEntity.getBody());
            String errorMessage = getPaystackMessageFromBody(mapper, verifyResponseEntity.getBody());
	        if (paystackVerificationResponse.getData() != null ) {
		        errorMessage = paystackVerificationResponse.getData().getGatewayResponse();
	        }
            throw new PaystackServiceHttpErrorException(errorMessage , verifyResponseEntity.getStatusCodeValue());
        }

	    log.info("verify response " + verifyResponseEntity + " .Transaction id=" + request.getTransactionId());

        return paystackVerificationResponse;
    }

    private Long saveUserCard(AuthorizationData cardSource, Long transactionId, String binsToDisable)
    {
        try {

            if (cardSource == null) throw new Exception("Authorization data is null.");

            UserCard userCard = UserCard.builder()
                    .providerData(cardSource.getAuthorizationCode())
                    .reference(cardSource.getSignature())
                    .lastFourDigits(cardSource.getLastFour())
                    .bin(cardSource.getBin())
                    .expiryDate(String.format("%02d/%02d", cardSource.getExpiryMonth(), cardSource.getExpiryYear() % 100))
                    .scheme(cardSource.getCardType())
                    .name(cardSource.getAccountName())
                    .status(getCardBankStatus(cardSource.getBin(), binsToDisable))
                    .isDefault(true)
                    .isActive(cardSource.getReusable())
                    .build();

            return cashierService.saveUserCard(transactionId, userCard);

        } catch (Exception e) {
            log.error("Failed to save user card. TransactionId: " + transactionId + "Exception: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean addCardRemark(Long transactionId, AuthorizationData cardSource) {
        if (cardSource == null) return false;

        try {
            UserCard userCard = UserCard.builder()
                    .providerData(cardSource.getAuthorizationCode())
                    .reference(cardSource.getSignature())
                    .lastFourDigits(cardSource.getLastFour())
                    .bin(cardSource.getBin())
                    .expiryDate(String.format("%02d/%02d", cardSource.getExpiryMonth(), cardSource.getExpiryYear() % 100))
                    .scheme(cardSource.getCardType())
                    .name(cardSource.getAccountName())
                    .bank(cardSource.getBank())
                    .isDefault(true)
                    .isActive(cardSource.getReusable())
                    .build();

            cashierService.addCardRemark(transactionId, null, userCard, TransactionRemarkType.ACCOUNT_DATA);
        } catch (Exception e) {
            log.error("Failed to save user card. TransactionId: " + transactionId + "Exception: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    private PaymentMethodStatusType getCardBankStatus(String bin, String binsToDisable) {
        if (bin == null || bin.isEmpty() || binsToDisable == null || binsToDisable.isEmpty()) {
            return PaymentMethodStatusType.ACTIVE;
        }

        return Arrays.stream(binsToDisable.split(",")).anyMatch(b -> b.trim().equals(bin))
                ? PaymentMethodStatusType.DISABLED
                : PaymentMethodStatusType.ACTIVE;
    }
}
