package lithium.service.casino.provider.sportsbook.shared.service;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.enums.ReservationStatus;
import lithium.service.casino.provider.sportsbook.shared.context.ContextWithReservation;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    UserRepository userRepository;

    public void findReservation(ContextWithReservation context) throws Status444ReferencedEntityNotFound,
            Status500InternalServerErrorException, Status438ReservationPendingException {
        Reservation reservation;
        try {
            /**
             * LSPLAT-2789 - We needed to add a lock on the reservation in addition to the lock on the user, since there were debit reserves for same reserveId
             *               and this resulted in changes trying to be made to an out of date reservation object. Therefore locking the reservation also, solved
             *               this issue, whereby each thread racing to update reservation would always work on the latest entity.
             */
            reservation = reservationRepository.findByReserveIdAlwaysLock(context.getReservationId());
        } catch (CannotAcquireLockException e) {
            throw new Status500InternalServerErrorException("Unable to lock reservation. Did you send more than one " +
                    "request for the same reservation at the same time?", e);
        }
        if (reservation == null) throw new Status444ReferencedEntityNotFound("Reservation with the supplied ID not found");
        if (reservation.getReservationStatus().getName().contentEquals(ReservationStatus.PENDING.name()) ||
                reservation.getReservationStatus().getName().contentEquals(ReservationStatus.TIMEOUT.name())
        ) {
           throw new Status438ReservationPendingException();
        }
        context.setReservation(reservation);
        context.setDomain(reservation.getUser().getDomain());
        context.setUser(reservation.getUser());
        try {
            userRepository.findByGuidAlwaysLock(reservation.getUser().getGuid());
        } catch (CannotAcquireLockException e) {
            throw new Status500InternalServerErrorException("Unable to lock user. Did you send more than one " +
                    "request for the same user at the same time?", e);
        }
        context.setCurrency(reservation.getCurrency());
        log.debug("findReservation " + context);
    }

}
