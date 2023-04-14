package lithium.service.cashier.processor.trustly.api.data.response;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lombok.Data;

@Data
public class Result {
    private String signature;
    private String uuid;
    private Method method;
    private Object data;
}
