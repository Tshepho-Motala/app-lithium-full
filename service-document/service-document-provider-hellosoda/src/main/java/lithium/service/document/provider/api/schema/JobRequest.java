package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    @JsonProperty("application_id")
    private String applicationId;
    private User data;
    @JsonProperty("consumer_id")
    private String consumerId;
    @JsonProperty("notify_url")
    private String notifyUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Externals externals;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> tokens;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean commit;

}
