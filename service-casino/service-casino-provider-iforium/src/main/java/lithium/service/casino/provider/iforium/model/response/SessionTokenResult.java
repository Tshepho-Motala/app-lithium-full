package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionTokenResult {

    @JsonProperty("SessionToken")
    private String sessionToken;
}