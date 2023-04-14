package lithium.service.cashier.processor.interswitch;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.interswitch.services.DepositService;
import lithium.service.cashier.processor.interswitch.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServiceCashierInterswitchDoProcessor extends DoProcessorAdapter {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private DepositService depositService;

    @Override
    protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return depositService.initiateWebDeposit(request, response);
        } catch (Exception e) {
            String message = "Deposit initialization failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            response.setDeclineReason("Wrong input data. " + e.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    @Override
    protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return depositService.verifyWebDeposit(request, response).getBody();
        } catch (Exception e) {
            String message = "Deposit verification failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        }
    }

    @Override
    public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return withdrawService.withdraw(request, response, rest);
        } catch (Exception e) {
            String message = "Withdraw initialization failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            response.setDeclineReason(e.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return withdrawService.verify(request, response, rest);
        } catch (Exception e) {
            String message = "Withdraw verification failed (" + request.getTransactionId() + ") due " + e.getMessage();
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setMessage(e.getMessage());
            return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        }
    }
}
