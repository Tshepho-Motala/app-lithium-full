package lithium.service.cashier.processor.paystack.services;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.processor.paystack.exeptions.PaystackWrongConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
public abstract class BasePaystackService {
    protected String property(String key, DoProcessorRequest request) throws PaystackWrongConfigurationException {
        try {
            return request.getProperty(key);
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new PaystackWrongConfigurationException(e.getMessage());
        }
    }
    protected String inputData(String key, DoProcessorRequest request) throws PaystackWrongConfigurationException {
        try {
            return request.stageInputData(1, key);
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new PaystackWrongConfigurationException(e.getMessage());
        }
    }
    protected int inputCents(DoProcessorRequest request) throws PaystackWrongConfigurationException {
        try {
            return request.inputAmountCents().intValue();
        } catch (Exception e) {
            log.error(e.getMessage() + "(" + request.getTransactionId() + ")");
            throw new PaystackWrongConfigurationException(e.getMessage());
        }
    }

    protected MultiValueMap<String, String> prepareHeaders(DoProcessorRequest request) throws PaystackWrongConfigurationException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + property("secret_key", request));
        headers.add("content-type", "application/json");
        headers.add("User-Agent", "Paystack-Developers-Hub");
        return headers;
    }
}
