package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameRoundResponse extends Response {

    @JsonProperty("Balance")
    private Balance balance;

    @JsonProperty("Result")
    private GameRoundResult result;
}
