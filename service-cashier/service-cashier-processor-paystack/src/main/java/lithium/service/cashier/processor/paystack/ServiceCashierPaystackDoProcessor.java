package lithium.service.cashier.processor.paystack;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.paystack.services.DepositService;
import lithium.service.cashier.processor.paystack.services.DepositVerifyService;
import lithium.service.cashier.processor.paystack.services.WithdrawService;
import lithium.service.cashier.processor.paystack.services.WithdrawVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@Slf4j
public class ServiceCashierPaystackDoProcessor extends DoProcessorAdapter {

    @Autowired
    WithdrawVerifyService withdrawVerifyService;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    DepositService depositService;

    @Autowired
    DepositVerifyService depositVerifyService;

    @Override
    protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        try {
            if (request.getMethodCode().equals("paystackussd")) {
                return depositService.initiateUssdDeposit(request, response, rest);
            } else if (request.stageInputData(1).get("cardReference") != null) {
                return depositService.recurringWebDeposit(request, response, rest);
            } else {
                return depositService.initiateWebDeposit(request, response, rest);
            }
        } catch (Exception e) {
            String message = "Deposit initialization failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }


    @Override
    protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            if (request.getMethodCode().equals("paystackussd")) {
                 if (!request.stageOutputData(1).containsKey("skipVerify") || !Boolean.parseBoolean(request.stageOutputData(1).get("skipVerify").toString())) {
                     return depositVerifyService.verifyUssdRequest(request, response);
                 } else {
                     response.setOutputData(1, "skipVerify", "false");
                     return DoProcessorResponseStatus.NOOP;
                 }
            } else {
                return depositVerifyService.verify(request, response, false);
            }
        } catch (Exception e) {
            String message = "Deposit verification failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            return DoProcessorResponseStatus.NOOP;
        }
    }

    @Override
    public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return withdrawService.withdraw(request, response, rest);
        } catch (Exception e) {
            String message = "Withdraw  initialization failed (" + request.getTransactionId() + ") due " + e.getMessage()
                    + ". Moved to next stage to check transaction status on Paystack side";
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
        }
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return withdrawVerifyService.verify(request, response);
        } catch (Exception e) {
            String message = "Withdraw verification failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            return DoProcessorResponseStatus.NOOP;
        }
    }
}
