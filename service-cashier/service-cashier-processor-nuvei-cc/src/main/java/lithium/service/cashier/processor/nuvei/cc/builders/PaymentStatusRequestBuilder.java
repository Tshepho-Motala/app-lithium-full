package lithium.service.cashier.processor.nuvei.cc.builders;

import com.safecharge.model.Card;
import com.safecharge.model.PaymentOption;
import com.safecharge.model.ThreeD;
import com.safecharge.request.GetPaymentStatusRequest;
import com.safecharge.request.PaymentRequest;
import com.safecharge.request.SafechargeBaseRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusRequestBuilder extends RequestBuilder {

    public SafechargeBaseRequest getRequest(DoProcessorRequest request) throws Exception {
        return  GetPaymentStatusRequest.builder()
            .addSessionToken(request.stageOutputData(1, "session_token"))
            .addMerchantInfo(getMerchantInfo(request))
            .build();
    }
}
