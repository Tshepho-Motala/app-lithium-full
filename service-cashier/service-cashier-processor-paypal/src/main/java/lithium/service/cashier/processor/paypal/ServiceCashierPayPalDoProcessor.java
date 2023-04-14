package lithium.service.cashier.processor.paypal;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.paypal.services.DepositService;
import lithium.service.cashier.processor.paypal.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class ServiceCashierPayPalDoProcessor extends DoProcessorAdapter {

    @Autowired
    private DepositService depositService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    MessageSource messageSource;

    @Override
    protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        try {
            return depositService.initiateDeposit(request, response, rest);
        } catch (Exception e) {
            String message = "Can't initiate PayPalDeposit (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message, e);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    @Override
    protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        try {
            String payerId = null;
            if (nonNull(request.getProcessorAccount())) {
                log.info("Request contain processor account(" + request.getProcessorAccount().getId() + "), proceed deposit with BA");
                payerId = request.getProcessorAccount().getReference();
            }
            return depositService.captureOrder(request, response, rest);
        } catch (Exception e) {
            String message = "Can't capture PayPal order (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message, e);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.NOOP;
        }
    }


    @Override
    public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        try {
            return withdrawService.initiateWithdrawal(request, response, rest);
        } catch (Exception e) {
            String message = "Can't initiate PayPalWithdrawal (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message, e);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        try {
            return withdrawService.checkWithdrawalStatus(request, response, rest);
        } catch (Exception e) {
            String message = "Can't check PayPal withdraw status (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message, e);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.NOOP;
        }
    }
}
