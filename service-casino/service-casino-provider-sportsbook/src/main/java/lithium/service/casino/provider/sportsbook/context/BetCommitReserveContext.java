package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve.BetCommitReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve.BetCommitReserveResponse;
import lithium.service.casino.provider.sportsbook.shared.context.ContextWithReservation;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCommit;
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
public class BetCommitReserveContext implements ContextWithReservation {
    String locale;
    User user;
    Domain domain;
    Currency currency;
    Reservation reservation;
    ReservationCommit reservationCommit;
    BetCommitReserveRequest request;
    BetCommitReserveResponse response;
    String convertedGuid;

    @Override
    public Long getReservationId() {
        return request.getReserveId();
    }
}
