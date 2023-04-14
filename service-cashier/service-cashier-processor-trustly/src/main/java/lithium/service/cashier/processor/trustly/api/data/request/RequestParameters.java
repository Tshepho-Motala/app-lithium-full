package lithium.service.cashier.processor.trustly.api.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestParameters {
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("UUID")
    private String uuid;
    @JsonProperty("Data")
    private RequestData data;
}
