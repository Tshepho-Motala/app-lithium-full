package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedeemSessionTokenResponse extends Response {

    @JsonProperty("Result")
    private Result result;

    public RedeemSessionTokenResponse(Integer errorCode, Result result) {
        super(errorCode);
        this.result = result;
    }
}
