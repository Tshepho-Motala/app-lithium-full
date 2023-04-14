package lithium.service.casino.provider.roxor.api.schema.balance;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetPlayerBalanceRequest {
    private String playerId;
    private String website;
    private String gameKey;
}
