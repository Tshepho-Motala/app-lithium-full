package lithium.service.cashier.processor.smartcash.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.util.StringUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmartcashConnectionError {
    @JsonProperty("status_message")
    private String statusMessage;
    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("error_description")
    private String errorDescription;
    private String error;
    @JsonIgnore
    public String getDescription() {
        return !StringUtil.isEmpty(statusCode)
            ? statusCode + ":" + statusMessage
            : errorDescription;
    }
}
