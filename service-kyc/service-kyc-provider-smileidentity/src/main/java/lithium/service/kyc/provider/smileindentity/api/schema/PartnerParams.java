package lithium.service.kyc.provider.smileindentity.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerParams {
    @JsonProperty("job_id")
    private String jobId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("job_type")
    private Integer jobType;

}
