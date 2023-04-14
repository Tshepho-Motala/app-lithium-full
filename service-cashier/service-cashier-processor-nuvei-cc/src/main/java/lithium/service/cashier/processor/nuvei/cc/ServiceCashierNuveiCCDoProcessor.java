package lithium.service.cashier.processor.nuvei.cc;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCDepositApiService;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCWithdrawApiService;
import lithium.service.cashier.processor.nuvei.exceptions.NuveiVerifyTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServiceCashierNuveiCCDoProcessor extends DoProcessorAdapter {

    @Autowired
    NuveiCCDepositApiService depositApiService;
    @Autowired
    NuveiCCWithdrawApiService withdrawApiService;

    @Override
    protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        return depositApiService.initPayment(request, response);
    }

    @Override
    protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        //if transaction is expired on this stage Sale was not created yet.
        return request.isTransactionExpired() ? DoProcessorResponseStatus.NOOP : depositApiService.payment(request, response);
    }

    @Override
    protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return depositApiService.verifyTransaction(request, response);
        } catch (NuveiVerifyTransactionException e) {
            return DoProcessorResponseStatus.NOOP;
        }
    }

    @Override
    public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        return withdrawApiService.payout(request, response, context, rest);
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return withdrawApiService.verifyTransaction(request, response);
        } catch (NuveiVerifyTransactionException e) {
            return DoProcessorResponseStatus.NOOP;
        }
    }
}
