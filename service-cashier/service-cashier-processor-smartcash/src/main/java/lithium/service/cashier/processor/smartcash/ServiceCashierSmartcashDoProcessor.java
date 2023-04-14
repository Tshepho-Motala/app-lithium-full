package lithium.service.cashier.processor.smartcash;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;

import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.smartcash.data.SmartcashResponseStatus;
import lithium.service.cashier.processor.smartcash.data.SmartcashAuthorizationResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import lithium.service.cashier.processor.smartcash.data.TransactionResponseData;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashResponseCodes;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashTransactionStatus;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashException;
import lithium.service.cashier.processor.smartcash.services.SmartcashApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ServiceCashierSmartcashDoProcessor extends DoProcessorAdapter {
	@Autowired
	LithiumConfigurationProperties lithiumProperties;

	@Autowired
	SmartcashApiService smartcashApiService;

	@Autowired
	MessageSource messageSource;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		try {
			response.setPaymentType("ussd");
			SmartcashAuthorizationResponse authResponse = smartcashApiService.getAuthorizationToken(request, response);
			String msisdn = smartcashApiService.msisdnFromMobile(request.getUser().getTelephoneNumber(), Integer.parseInt(request.getProperty("msisdn_length")));
			String walletId = smartcashApiService.getCustomerWallet(request, response, authResponse.getAccessToken(), msisdn);

			SmartcashPaymentResponse paymentResponse = smartcashApiService.payment(request, response, authResponse.getAccessToken(), msisdn, walletId);

			SmartcashResponseStatus paymentStatus = paymentResponse.getStatus();
			if (!paymentStatus.isSuccess()) {
				response.setDeclineReason(paymentStatus.getCode() + ": " + paymentStatus.getMessage());
				response.setErrorCode(SmartcashResponseCodes.fromResponseCode(paymentStatus.getResponseCode()).getGeneralError().getCode());
				response.setMessage(SmartcashResponseCodes.fromResponseCode(paymentStatus.getResponseCode()).getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
				return DoProcessorResponseStatus.DECLINED;
			}
			TransactionResponseData smartcashTransaction = paymentResponse.getData().getTransaction();
			SmartcashTransactionStatus transactionStatus = SmartcashTransactionStatus.fromCode(smartcashTransaction.getStatus());
			if (transactionStatus != SmartcashTransactionStatus.TS && transactionStatus != SmartcashTransactionStatus.TIP) {
				response.setDeclineReason(smartcashTransaction.getMessage());
				response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
				response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
				return DoProcessorResponseStatus.DECLINED;
			}
			response.setProcessorReference(smartcashTransaction.getSmartcashMoneyId());
			response.setOutputData(1, "status", smartcashTransaction.getMessage());

			return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		} catch (SmartcashException smartcashException) {
			response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
			response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			response.setDeclineReason(smartcashException.getMessage());
			return DoProcessorResponseStatus.DECLINED;
		} catch (Throwable e) {
			response.setDeclineReason("Failed to initiate Smartcash payment");
			response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
			response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			log.error("Failed to initiate Smartcash payment for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
			response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return verifyTransaction(request, response);
	}

	@Override
	public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			response.setPaymentType("ussd");
			SmartcashAuthorizationResponse authResponse = smartcashApiService.getAuthorizationToken(request, response);
			String msisdn = smartcashApiService.msisdnFromMobile(request.getUser().getTelephoneNumber(), Integer.parseInt(request.getProperty("msisdn_length")));
			String walletId = smartcashApiService.getCustomerWallet(request, response, authResponse.getAccessToken(), msisdn);
			SmartcashPaymentResponse paymentResponse = smartcashApiService.payout(request, response, authResponse.getAccessToken(), msisdn, walletId);

			SmartcashResponseStatus paymentStatus = paymentResponse.getStatus();
			if (!paymentStatus.isSuccess()) {
				response.setDeclineReason(paymentStatus.getCode() + ": " + paymentStatus.getMessage());
				response.setErrorCode(SmartcashResponseCodes.fromResponseCode(paymentStatus.getResponseCode()).getGeneralError().getCode());
				response.setMessage(SmartcashResponseCodes.fromResponseCode(paymentStatus.getResponseCode()).getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
				return DoProcessorResponseStatus.DECLINED;
			}
			TransactionResponseData smartcashTransaction = paymentResponse.getData().getTransaction();
			response.setProcessorReference(smartcashTransaction.getSmartcashMoneyId());
			response.setOutputData(1, "status", smartcashTransaction.getMessage());

			SmartcashTransactionStatus transactionStatus = SmartcashTransactionStatus.fromCode(smartcashTransaction.getStatus());
			if (transactionStatus != SmartcashTransactionStatus.TS && transactionStatus != SmartcashTransactionStatus.TIP) {
				response.setDeclineReason(smartcashTransaction.getMessage());
				response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
				response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
				return DoProcessorResponseStatus.DECLINED;
			}
			return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		} catch (SmartcashException smartcashException) {
			response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
			response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			response.setDeclineReason(smartcashException.getMessage());
			return DoProcessorResponseStatus.DECLINED;
		} catch (Throwable e) {
			response.setDeclineReason("Failed to initiate Smartcash payment");
			response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
			response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			log.error("Failed to initiate Smartcash payment for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
			response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return verifyTransaction(request, response);
	}

	private DoProcessorResponseStatus verifyTransaction(DoProcessorRequest request, DoProcessorResponse response) {
		try {
			return smartcashApiService.verifyTransaction(request, response);
		} catch (Exception e) {
			return DoProcessorResponseStatus.NOOP;
		}
	}
}
