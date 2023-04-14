package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitRequest {
    private Externals externals;
    @JsonProperty("report_parameters")
    private ReportParameters reportParameters;
    private Boolean commit;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportParameters {
        private String duration;
    }
}
