package lithium.service.user.provider.sphonic.idin.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class RequestData {
    @JsonProperty("Applicant_Reference")
    private String applicantReference;
    @JsonProperty("Return_URL")
    private String returnUrl;
}
