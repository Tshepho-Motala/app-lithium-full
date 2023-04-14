package lithium.service.cashier.processor.nuvei.cc.builders;

import com.safecharge.model.UserPaymentOption;
import com.safecharge.request.PayoutRequest;
import com.safecharge.request.SafechargeBaseRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import org.springframework.stereotype.Service;

@Service
public class PayoutRequestBuilder extends RequestBuilder {

    private UserPaymentOption getUserPaymentOptions(DoProcessorRequest request) {
        UserPaymentOption paymentOption = new UserPaymentOption();
        paymentOption.setUserPaymentOptionId(request.getProcessorAccount().getReference());
        return paymentOption;
    }

    public SafechargeBaseRequest getRequest(DoProcessorRequest request) throws Exception {
        return PayoutRequest.builder()
            .addMerchantInfo(getMerchantInfo(request))
            .addUserTokenId(request.getUser().getRealGuid())
            .addClientUniqueId(request.getTransactionId().toString())
            .addClientRequestId(environment + request.getTransactionId().toString())
            .addDeviceDetails(getDeviceDetails(request))
            .addAmountAndCurrency(request.inputAmount().toString(), request.getUser().getCurrency())
            .addUserPaymentOption(getUserPaymentOptions(request))
            .addUrlDetails(getUrlDetails(request))
            .build();
    }
}
