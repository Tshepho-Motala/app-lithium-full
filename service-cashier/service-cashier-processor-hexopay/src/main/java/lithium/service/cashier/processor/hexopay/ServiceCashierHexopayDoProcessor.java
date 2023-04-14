package lithium.service.cashier.processor.hexopay;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.hexopay.services.HexopayGatawayApiService;
import lithium.service.cashier.processor.hexopay.services.HexopayPageApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServiceCashierHexopayDoProcessor extends DoProcessorAdapter {

    @Autowired
    private HexopayGatawayApiService gatewayService;
    @Autowired
    private HexopayPageApiService pageService;

    @Autowired
    MessageSource messageSource;

    @Override
    protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        return request.getProcessorAccount() == null
                ? pageService.initiateWebDeposit(request, response, rest)
                : gatewayService.cardReuseDeposit(request, response, rest);
    }

    @Override
    protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        return gatewayService.updateTransactionResponse(request, response, rest);
        /*return request.stageOutputData(1).containsKey("paymentToken")
                ? pageService.checkPaymentToken(request, response, rest)
                : gatewayService.updateTransaction(request, response, rest);*/
    }


    @Override
    public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
         return gatewayService.payout(request, response, rest);
    }

    @Override
    protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        return gatewayService.updateTransactionResponse(request, response, rest);
    }
}
