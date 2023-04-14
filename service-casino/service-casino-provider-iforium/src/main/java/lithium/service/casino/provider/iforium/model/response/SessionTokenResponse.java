package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionTokenResponse extends Response {

    @JsonProperty("Result")
    private SessionTokenResult result;

    @Builder
    public SessionTokenResponse(Integer errorCode, SessionTokenResult result) {
        super(errorCode);
        this.result = result;
    }
}
