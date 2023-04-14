package lithium.service.casino.provider.slotapi.context;

import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequestKindEnum;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultResponse;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BetResultContext {
    private String domainName;
    private String userGuid;
    private BetRound betRound;
    private BetResultRequest request;
    private BetResultResponse response;
    private BetResult betResult;
}
