package lithium.service.casino.provider.sportsbook.services;

import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.SystemTransactionClient;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationStatusRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PendingReservationService {
	@Autowired private ReservationRepository reservationRepository;
	@Autowired private ReservationStatusRepository reservationStatusRepository;
	@Autowired private LithiumServiceClientFactory services;

	@Value("${lithium.services.casino.provider.sportsbook.pending-reservation-job.fetch-size:10}")
	private Integer fetchSize;

	@Value("${lithium.services.casino.provider.sportsbook.pending-reservation-job.expire-after-ms:3600000}")
	private Integer expireAfterMs;

	@TimeThisMethod
	public void process() {
		ReservationStatus timeout = reservationStatusRepository.findOrCreateByName(
				lithium.service.casino.provider.sportsbook.enums.ReservationStatus.TIMEOUT.name(),
				() -> new ReservationStatus());
		ReservationStatus completed = reservationStatusRepository.findOrCreateByName(
				lithium.service.casino.provider.sportsbook.enums.ReservationStatus.COMPLETED.name(),
				() -> new ReservationStatus());

		int page = 0;
		boolean process = true;
		while (process) {
			Pageable pageRequest = PageRequest.of(page, fetchSize);
			Page<Reservation> pageResult = reservationRepository.findByReservationStatusOrderByAccountingLastRecheckedAsc(
					timeout, pageRequest);
			for (Reservation reservation: pageResult.getContent()) {
				long msSinceCreated = (new Date().getTime() - reservation.getCreatedDate());
				if (msSinceCreated > expireAfterMs) {
					log.warn("Reservation is " + msSinceCreated + "ms old. Abandoning reservation"
							+ " [reservation="+reservation+"]");
					reservationRepository.delete(reservation);
				} else {
					try {
						List<TransactionEntry> transactionEntries = getSystemTransactionClient().get()
								.findTransactionEntries(String.valueOf(reservation.getReserveId()),
								"SPORTS_RESERVE");
						if (transactionEntries != null && !transactionEntries.isEmpty()) {
							TransactionEntry transactionEntry = transactionEntries.get(0);
							reservation.setAccountingTransactionId(transactionEntry.getTransactionId());
							reservation.setBalanceAfter(getPlayerBalance(transactionEntries));
							reservation.setReservationStatus(completed);
						}
					} catch (Exception e) {
						log.error("Failed to get transaction entries [reservation="+reservation+"] " + e.getMessage(), e);
					} finally {
						reservation.setAccountingLastRechecked(new Date());
						reservationRepository.save(reservation);
					}
				}
			}
			page++;
			if (!pageResult.hasNext()) process = false;
		}
	}

	private Double getPlayerBalance(List<TransactionEntry> transactionEntries) {
		Optional<Double> playerBalance = Optional.ofNullable(
			transactionEntries.stream()
			.filter(te -> te.getAccount().getAccountCode().getCode().contentEquals("PLAYER_BALANCE"))
			.findFirst()
			.map(te -> CurrencyAmount.fromCents(te.getPostEntryAccountBalanceCents()).toAmountReverse().doubleValue())
		)
		.orElse(null);
		return (playerBalance.isPresent() ? playerBalance.get().doubleValue() : null);
	}

	private Optional<SystemTransactionClient> getSystemTransactionClient() {
		return getClient(SystemTransactionClient.class, "service-accounting");
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}
}

