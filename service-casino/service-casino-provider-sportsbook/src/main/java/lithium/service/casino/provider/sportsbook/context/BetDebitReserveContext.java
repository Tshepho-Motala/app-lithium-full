package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve.BetDebitReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve.BetDebitReserveResponse;
import lithium.service.casino.provider.sportsbook.shared.context.ContextWithReservation;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BetDebitReserveContext implements ContextWithReservation {
    String locale;
    User user;
    Domain domain;
    Currency currency;
    Reservation reservation;
    Bet bet;
    BetDebitReserveRequest request;
    BetDebitReserveResponse response;
    String convertedGuid;

    @Override
    public Long getReservationId() {
        return request.getReserveId();
    }
}
