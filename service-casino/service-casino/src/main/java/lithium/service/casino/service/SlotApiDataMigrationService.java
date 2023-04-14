package lithium.service.casino.service;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.client.SlotApiDataMigrationClient;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.data.entities.Bet;
import lithium.service.casino.data.entities.BetRequestKind;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetResultKind;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Currency;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.entities.SlotApiDataMigration;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.repositories.BetRepository;
import lithium.service.casino.data.repositories.BetRequestKindRepository;
import lithium.service.casino.data.repositories.BetResultKindRepository;
import lithium.service.casino.data.repositories.BetResultRepository;
import lithium.service.casino.data.repositories.BetRoundRepository;
import lithium.service.casino.data.repositories.CurrencyRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameRepository;
import lithium.service.casino.data.repositories.ProviderRepository;
import lithium.service.casino.data.repositories.SlotApiDataMigrationRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SlotApiDataMigrationService {
	@Autowired private BetRepository betRepository;
	@Autowired private BetRequestKindRepository betRequestKindRepository;
	@Autowired private BetResultRepository betResultRepository;
	@Autowired private BetResultKindRepository betResultKindRepository;
	@Autowired private BetRoundRepository betRoundRepository;
	@Autowired private CurrencyRepository currencyRepository;
	@Autowired private DomainRepository domainRepository;
	@Autowired private GameRepository gameRepository;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private ProviderRepository providerRepository;
	@Autowired private SlotApiDataMigrationRepository slotApiDataMigrationRepository;
	@Autowired private UserRepository userRepository;

	@Value("${lithium.services.casino.slot-api-data-migration-job.data-end-id}")
	private Long dataEndId;

	@Value("${lithium.services.casino.slot-api-data-migration-job.data-fetch-size:100}")
	private Long dataFetchSize;

	@TimeThisMethod
	public void migrate() {
		SlotApiDataMigration migration = slotApiDataMigrationRepository.findOne(1L);
		if (migration == null) {
			migration = slotApiDataMigrationRepository.save(
				SlotApiDataMigration.builder()
						.id(1L)
						.currentId(null)
						.build()
			);
		}
		if (migration.isProcessing()) {
			log.warn("SlotApi data migration is still processing. " +
					"Perhaps increase lithium.services.casino.slot-api-data-migration-job.delay-ms " +
					"OR decrease lithium.services.casino.slot-api-data-migration-job.data-fetch-size? " +
					"lithum_casino.slot_api_data_migration.processing could potentially also be stuck if the service " +
					"restarted whilst migration was busy processing | [migration="+migration+"]");
			return;
		}
		if (dataEndId == null) {
			log.warn("SlotApiDataMigrationService.migrate lithium.services.casino.slot-api-data-migration-job.data-end-id not set.");
			return;
		}
		SW.start("SlotApiDataMigrationService.migrate.fetchdata");
		if (migration.getCurrentId() != null &&
				migration.getCurrentId().longValue() == dataEndId.longValue()) {
			log.warn("SlotApi data migration is complete. " +
					"Switch lithium.services.casino.slot-api-data-migration-job.enabled to false to avoid unnecessary " +
					"lookups to lithium_casino.slot_api_data_migration.");
			return;
		}
		migration.setProcessing(true);
		migration = slotApiDataMigrationRepository.save(migration);
		long start = (migration.getCurrentId() != null) ? migration.getCurrentId() + 1 : 1;
		long end = start + dataFetchSize;
		log.info("SlotApiDataMigrationService | Fetching bets with ID's between " + start + " and " + end);
		List<lithium.service.casino.client.objects.slotapi.Bet> data = getSlotApiDataMigrationClient().get()
				.fetchBets(start, end);
		SW.stop();
		SW.start("SlotApiDataMigrationService.migrate.persistdata");
		if (data.isEmpty()) {
			// Track the end ID that we just checked, that way the next run will add the dataFetchSize onto this value.
			migration.setCurrentId(end);
		}
		for (lithium.service.casino.client.objects.slotapi.Bet b: data) {
			persist(b);
			migration.setCurrentId(b.getId());
			migration = slotApiDataMigrationRepository.save(migration);
		}
		SW.stop();
		migration.setProcessing(false);
		migration = slotApiDataMigrationRepository.save(migration);
	}

	private void persist(lithium.service.casino.client.objects.slotapi.Bet b) {
		String domainName = b.getBetRound().getUser().getDomain().getName();
		Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());

		Provider provider = providerRepository.findOrCreateByGuid(domainName + "/service-casino-provider-slotapi",
				() -> Provider.builder().domain(domain).build());

		lithium.service.casino.client.objects.slotapi.BetRound br = b.getBetRound();
		BetRound betRound = betRoundRepository.findByProviderAndGuid(provider, br.getGuid());
		if (betRound == null) {
			String gameGuid = br.getUser().getDomain().getName() + "/service-casino-provider-slotapi_"
					+ br.getGame().getGuid();
			Game game = gameRepository.findOrCreateByGuid(gameGuid, () -> new Game());
			User user = userRepository.findOrCreateByGuid(br.getUser().getGuid(), () -> User.builder().domain(domain)
					.build());

			betRound = BetRound.builder()
					.guid(br.getGuid())
					.complete(br.isComplete())
					.provider(provider)
					.game(game)
					.user(user)
					.createdDate(br.getCreatedDate())
					.modifiedDate(br.getModifiedDate())
					.sequenceNumber(br.getSequenceNumber())
					.roundReturnsTotal(0)
					.build();
			betRound = betRoundRepository.save(betRound);

			// Slotapi has a single bet result
			if (b.getBetRound().getBetResult() != null) {
				lithium.service.casino.client.objects.slotapi.BetResult bres = br.getBetResult();
				BetResultKind betResultKind = betResultKindRepository.findOrCreateByCode(
						bres.getBetResultKind().getCode(), () -> new BetResultKind());
				Currency currency = currencyRepository.findOrCreateByCode(bres.getCurrency().getCode(),
						() -> new Currency());
				BetResult betResult = BetResult.builder()
						.createdDate(bres.getCreatedDate())
						.modifiedDate(bres.getModifiedDate())
						.provider(provider)
						.betResultTransactionId(bres.getBetResultTransactionId())
						.transactionTimestamp(bres.getTransactionTimestamp())
						.betRound(betRound)
						.betResultKind(betResultKind)
						.returns(bres.getReturns())
						.balanceAfter(bres.getBalanceAfter())
						.roundComplete(bres.isRoundComplete())
						.currency(currency)
						.lithiumAccountingId(bres.getLithiumAccountingId())
						.build();
				betResult = betResultRepository.save(betResult);
				betRound.setLastBetResult(betResult);
				betRound.setRoundReturnsTotal(betResult.getReturns());
				betRound = betRoundRepository.save(betRound);
			}
		}

		BetRequestKind betRequestKind = betRequestKindRepository.findOrCreateByCode(b.getKind().getCode(),
				() -> new BetRequestKind());
		Currency currency = currencyRepository.findOrCreateByCode(b.getCurrency().getCode(),
				() -> new Currency());
		Bet bet = Bet.builder()
				.createdDate(b.getCreatedDate())
				.modifiedDate(b.getModifiedDate())
				.provider(provider)
				.betRound(betRound)
				.betTransactionId(b.getBetTransactionId())
				.transactionTimestamp(b.getTransactionTimestamp())
				.kind(betRequestKind)
				.amount(b.getAmount())
				.balanceAfter(b.getBalanceAfter())
				.currency(currency)
				.lithiumAccountingId(b.getLithiumAccountingId())
				.build();
		bet = betRepository.save(bet);
	}

	private Optional<SlotApiDataMigrationClient> getSlotApiDataMigrationClient() {
		return getClient(SlotApiDataMigrationClient.class, "service-casino-provider-slotapi");
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
