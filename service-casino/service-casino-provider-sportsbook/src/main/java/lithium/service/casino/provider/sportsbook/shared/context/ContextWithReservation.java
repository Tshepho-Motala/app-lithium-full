package lithium.service.casino.provider.sportsbook.shared.context;

import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.User;

public interface ContextWithReservation {

    Long getReservationId();

    Reservation getReservation();
    void setReservation(Reservation reservation);

    Domain getDomain();
    void setDomain(Domain domain);

    User getUser();
    void setUser(User user);

    Currency getCurrency();
    void setCurrency(Currency currency);

}
