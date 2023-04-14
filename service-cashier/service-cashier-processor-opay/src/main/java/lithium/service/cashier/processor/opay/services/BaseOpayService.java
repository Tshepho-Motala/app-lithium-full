package lithium.service.cashier.processor.opay.services;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.processor.opay.exceptions.Status901InvalidOrMissingParameters;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseOpayService {
    
    protected String merchantId(DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        return property("merchant_id", request);
    }
    protected String rsaPrivate(DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        return property("rsa_private", request);
    }
    protected String baseUrl(DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        return property("base_v3_url", request);
    }
    
    private String property(String key, DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        try {
            return request.getProperty(key);
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new Status901InvalidOrMissingParameters(e.getMessage());
        }
    }
    protected String inputData(String key, DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        try {
            return request.stageInputData(1, key);
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new Status901InvalidOrMissingParameters(e.getMessage());
        }
    }
    protected int inputCents(DoProcessorRequest request) throws Status901InvalidOrMissingParameters {
        try {
            return request.inputAmountCents().intValue();
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new Status901InvalidOrMissingParameters(e.getMessage());
        }
    }
}
