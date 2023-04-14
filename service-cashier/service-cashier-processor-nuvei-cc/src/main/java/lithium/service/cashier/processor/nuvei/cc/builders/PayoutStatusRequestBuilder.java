package lithium.service.cashier.processor.nuvei.cc.builders;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.processor.nuvei.data.NuveiGetPayoutStatusRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PayoutStatusRequestBuilder extends RequestBuilder {

    public NuveiGetPayoutStatusRequest getRequest(DoProcessorRequest request) throws Exception {
        //not implemented in Nuvei Java SDK
        return NuveiGetPayoutStatusRequest.builder()
            .clientRequestId(environment + request.getTransactionId())
            .merchantId(request.getProperty("merchant_id"))
            .merchantSiteId(request.getProperty("merchant_site_id"))
            .timeStamp(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()))
            .build();
    }
}

