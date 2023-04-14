package lithium.service.casino.provider.slotapi.context;

import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequest;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetResponse;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lombok.Data;
import lombok.ToString;

import java.security.Principal;

@Data
@ToString
public class BetContext {
    private String domainName;
    private String userGuid;
    private BetRequest request;
    private BetResponse response;
    private Bet bet;
    private Long sessionId;
}
