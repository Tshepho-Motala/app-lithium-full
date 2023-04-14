package lithium.service.cashier.processor.trustly.api.data.response;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lombok.Data;

@Data
public class ErrorBody {
    private String signature;
    private String uuid;
    private Method method;
    private ErrorData data;
}
