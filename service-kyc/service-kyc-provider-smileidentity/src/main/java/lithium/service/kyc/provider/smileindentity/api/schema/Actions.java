package lithium.service.kyc.provider.smileindentity.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actions {
    @JsonProperty("Verify_ID_Number")
    private String verifyIdNumber;
    @JsonProperty("Return_Personal_Info")
    private String returnPersonalInfo;
}
