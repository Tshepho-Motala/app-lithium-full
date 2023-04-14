package lithium.service.cashier.processor.trustly.api.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lithium.service.cashier.processor.trustly.api.data.Method;
import lombok.Data;

@Data
public class Request {
    private Method method;
    private RequestParameters params;
    private double version = 1.1;
    @JsonIgnore
    public String getUUID() {
        return params.getUuid();
    }
}
