package lithium.service.casino.provider.sportsbook.data;

import java.util.List;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
public class SportsBetBonusResponse {
    @JsonProperty("error_code")
    String errorCode;
    @JsonProperty("error_message")
    String errorMessage;
    List<PlayerBonuses> playerBonuses;
}
