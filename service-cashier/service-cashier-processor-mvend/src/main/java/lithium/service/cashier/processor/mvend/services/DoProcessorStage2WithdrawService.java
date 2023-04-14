package lithium.service.cashier.processor.mvend.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.mvend.api.client.schema.PaymentAsyncRequest;
import lithium.service.cashier.processor.mvend.api.client.schema.PaymentAsyncResponse;
import lithium.service.cashier.processor.mvend.context.DoProcessorWithdrawContext;
import lithium.service.cashier.processor.mvend.services.shared.SharedService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class DoProcessorStage2WithdrawService {

    @Autowired
    CashierDoCallbackService cashierService;

    @Autowired
    UserApiInternalClientService userService;

    @Autowired
    SharedService sharedService;

    @TimeThisMethod
    public void doWithdraw(DoProcessorWithdrawContext context, RestTemplate restTemplate) throws Exception {

        sharedService.getPropertiesDMPFromServiceCashier(context, true, "mvend");

        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        String token = new Hash(
                context.getPropertiesDmp().getProperties().get("mvend_appid") +
                context.getPropertiesDmp().getProperties().get("mvend_apikey") +
                timestamp
        ).sha256();

        PaymentAsyncRequest request = new PaymentAsyncRequest();
        request.setAmount(context.getRequest().processorCommunicationAmount().toString());
        request.setAsync(true);
        request.setCurrency(context.getRequest().getUser().getCurrency());
        request.setMsisdn(context.getRequest().getUser().getCellphoneNumber());
        request.setRequesttype("makepaymentrequest");
        request.setTimestamp(timestamp);
        request.setToken(token);
        request.setTransactionref(context.getRequest().stageInputData(1, "mvend_reference"));

        log.info("doWithdraw request " + request);
        String url = context.getPropertiesDmp().getProperties().get("mvend_url") + "/paymentserver/async";
//        String url = "https://paymentasyncresponse.free.beeceptor.com/paymentserver/async";

        PaymentAsyncResponse response = restTemplate.postForObject(url, request, PaymentAsyncResponse.class);

        context.getResponse().addRawRequestLog(request.toString());
        context.getResponse().addRawResponseLog(response != null ? response.toString() : "No response received.");
        context.getResponse().setOutputData(2, "w-async-paymentRef", response.getPaymentref());
        context.getResponse().setAdditionalReference(response.getPaymentref());

        if (response.getStatus() == null) throw new Exception("Status is null " + response + " " + request);
        if (response.getStatus().contentEquals("Failed")) throw new Exception("Status is failed " + response + " " + request);
        if (response.getStatus().contentEquals("Error")) throw new Exception("Status is error " + response + " " + request);

        log.info("doWithdraw response " + response + " " + request);
    }

}
