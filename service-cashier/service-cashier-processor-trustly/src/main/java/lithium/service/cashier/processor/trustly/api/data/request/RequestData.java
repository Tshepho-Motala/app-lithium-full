package lithium.service.cashier.processor.trustly.api.data.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RequestData {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("Attributes")
    private Map<String, Object> attributes = new TreeMap<>();
}
