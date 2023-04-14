package lithium.service.user.provider.sphonic.idin.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Outcome {

    private String transactionResult;
    private String reason;
    private String authenticationAuthorityId;
    private String authenticationAuthorityName;
}
