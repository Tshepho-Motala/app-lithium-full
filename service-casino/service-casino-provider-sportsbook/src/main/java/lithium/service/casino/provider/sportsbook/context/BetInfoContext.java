package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.betinfo.BetInfoRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betinfo.BetInfoResponse;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveResponse;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetInfoContext {
    String locale;
    User user;
    Domain domain;
    BetInfoRequest request;
    BetInfoResponse response;
}
