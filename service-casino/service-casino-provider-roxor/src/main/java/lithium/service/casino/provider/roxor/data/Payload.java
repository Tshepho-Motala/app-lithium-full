package lithium.service.casino.provider.roxor.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payload {

    private String gameplayId;
    private String website;
    @JsonProperty("gamekey")
    private String gameKey;
    private String gameId;
    private String provider;
    private String sessionId;
    private String playerId;
    private String country;
    @NonNull
    private GameplayOperationEventType type;
    private String currency;
    private BigDecimal amount;
    private String status;
    private String poolId;
    private String gameVersion;
    private String gameConfigVersion;
    private Integer lines;
    private Integer coinSize;
    private String gameType;

}
