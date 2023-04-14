package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobResponse {
    @JsonProperty("job_url")
    private String jobUrl;
    @JsonProperty("job_id")
    private String jobId;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("consumer_id")
    private String consumerId;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("reg_time")
    private String regTime;
    private Boolean connected;
    @JsonProperty("notify_url")
    private String notifyUrl;
    private Map<String, Source> sources;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Source {
        private Boolean connected;
        private Boolean active;
    }
}
