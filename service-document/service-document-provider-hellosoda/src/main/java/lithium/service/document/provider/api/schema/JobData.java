package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobData {
    private User data;
    @JsonProperty("job_url")
    private String jobUrl;
    @JsonProperty("job_id")
    private String jobId;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("report_url")
    private String reportUrl;
    private String status;
    @JsonProperty("consumer_id")
    private String consumerId;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("reg_time")
    private String regTime;
    private Boolean connected;
    private Source sources;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Source {
        private Boolean connected;
        private Boolean active;
        private String lastSuccessfulFetchAt;
        private LastFetchResult lastFetchResult;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LastFetchResult {
        private String fetchedAt;
        private Boolean success;
        private List<String> errors;
    }
}
