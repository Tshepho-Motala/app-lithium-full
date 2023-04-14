package lithium.service.cashier.processor.paynl;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.paynl.exceptions.PaynlValidatePayoutException;
import lithium.service.cashier.processor.paynl.services.PaynlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ServiceCashierPaynlDoProcessor extends DoProcessorAdapter {

    @Autowired
    PaynlService paynlService;

    @Override
    protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
        return paynlService.initiateWithdraw(request, response, context, rest);
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            return paynlService.verifyPayout(request, response, rest);
        } catch (PaynlValidatePayoutException ex) {
            return DoProcessorResponseStatus.NOOP;
        }
    }
}
