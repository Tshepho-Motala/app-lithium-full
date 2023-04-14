package lithium.service.user.provider.sphonic.idin.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown=true)
public class TraceData {

    @JsonProperty("responseDateTime")
    private String responseDateTime;

    @JsonProperty("sphonicTransactionId")
    private String sphonicTransactionId;

    @JsonProperty("bluemTransactionId")
    private String bluemTransactionId;

    @JsonProperty("livescoreApplicantId")
    private String livescoreApplicantId;

    @JsonProperty("livescoreRequestId")
    private String livescoreRequestId;
}
