package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveResponse;
import lithium.service.casino.provider.sportsbook.enums.ReservationStatus;
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
@NoArgsConstructor
@Builder
public class BetReserveContext {
    String locale;
    User user;
    Domain domain;
    Currency currency;
    Reservation reservation;
    ReservationStatus reservationStatus;
    BetReserveRequest request;
    BetReserveResponse response;
    String convertedGuid;
}
