package lithium.service.casino.provider.incentive.services;

import com.ibm.icu.text.RuleBasedNumberFormat;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.AccountingStandardAccountCodes;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import lithium.service.casino.provider.incentive.storage.entities.User;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementResultRepository;
import lithium.service.casino.provider.incentive.storage.specifications.BetSpecifications;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
@Slf4j
public class BetHistoryService {
	@Autowired BetRepository repository;
	@Autowired SettlementResultRepository settlementResultRepository;
	@Autowired UserApiInternalClientService userApiInternalClientService;
	@Autowired AccountingClientService accountingClientService;
	@Autowired MessageSource messageSource;

	public Iterable<SettlementResult> findAllResultCodes() {
		return settlementResultRepository.findAll();
	}

	@TimeThisMethod
	public Page<Bet> find(
		List<String> domains,
		String betId,
		String userGuid,
		String eventName,
		String sport,
		String market,
		String competition,
		Boolean isSettled,
		String settlementId,
		String settlementResult,
		Date betTimestampRangeStart,
		Date betTimestampRangeEnd,
		Date settlementTimestampRangeStart,
		Date settlementTimestampRangeEnd,
		boolean shouldEnrichDataWithUsername,
		String searchValue,
		Pageable pageable
	) {
		Specification<Bet> spec = null;

		spec = addToSpec(domains, spec, BetSpecifications::domains);
		spec = addToSpec(betId, spec, BetSpecifications::betId);
		spec = addToSpec(userGuid, spec, BetSpecifications::user);
		spec = addToSpec(eventName, spec, BetSpecifications::eventName);
		spec = addToSpec(sport, spec, BetSpecifications::sport);
		spec = addToSpec(market, spec, BetSpecifications::market);
		spec = addToSpec(competition, spec, BetSpecifications::competition);
		spec = addToSpec(isSettled, spec, BetSpecifications::isSettled);
		spec = addToSpec(settlementId, spec, BetSpecifications::settlementId);
		spec = addToSpec(settlementResult, spec, BetSpecifications::settlementResult);
		spec = addToSpec(betTimestampRangeStart, false, spec, BetSpecifications::betTimestampRangeStart);
		spec = addToSpec(betTimestampRangeEnd, true, spec, BetSpecifications::betTimestampRangeEnd);
		spec = addToSpec(settlementTimestampRangeStart, false, spec, BetSpecifications::settlementTimestampRangeStart);
		spec = addToSpec(settlementTimestampRangeEnd, true, spec, BetSpecifications::settlementTimestampRangeEnd);
		spec = addToSpec(searchValue, spec, BetSpecifications::any);

		Page<Bet> result = repository.findAll(spec, pageable);

		if (shouldEnrichDataWithUsername && !result.getContent().isEmpty()) {
			SW.start("enrichDataWithUsername");
			enrichDataWithUsername(result);
			SW.stop();
		}

		if (!result.getContent().isEmpty()) {
			SW.start("enrichDataWithBalance");
			enrichDataWithBalance(result);
			SW.stop();
		}

		enrichDataWithBetType(result);

		return result;
	}

	private void enrichDataWithBalance(Page<Bet> result) {
		result.forEach(bet -> {
			Long lithiumAccountingId = bet.getLithiumAccountingId();
			try {
				List<TransactionEntry> transactions = accountingClientService.transactions(lithiumAccountingId);
				if (transactions != null) {
					transactions.forEach(te -> {
						if (te.getAccount().getAccountCode().getCode().equals(AccountingStandardAccountCodes.PLAYER_BALANCE_ACCOUNT)) {
							bet.setPostEntryAccountBalance(CurrencyAmount.fromCents(te.getPostEntryAccountBalanceCents()).toAmountReverse());
						}
					});
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	private void enrichDataWithUsername(Page<Bet> result) {
		Map<String, List<User>> users = new LinkedHashMap<>();
		Set<String> guids = new LinkedHashSet<>();
		for (Bet bet: result.getContent()) {
			String guid = bet.getPlacement().getUser().getGuid();
			if (users.get(guid) != null) {
				List<User> usersList = users.get(guid);
				usersList.add(bet.getPlacement().getUser());
				users.put(guid, usersList);
			} else {
				List<User> usersList = new ArrayList<>();
				usersList.add(bet.getPlacement().getUser());
				users.put(guid, usersList);
			}
			guids.add(guid);
		}
		try {
			log.debug("enrichDataWithUsername guids.size: " + guids.size());
			Map<String, String> guidsToUsernameMappings = userApiInternalClientService.guidsToUsernameMappings(guids);
			guidsToUsernameMappings.forEach((guid, username) -> {
				List<User> usersList = users.get(guid);

				if(usersList != null) {
					usersList.stream().forEach(user -> { user.setUsername(username); });
				}

			});
		} catch (Status500UserInternalSystemClientException e) {
			log.warn("Unable to enrich data with usernames | " + e.getMessage());
		}
	}

	private void enrichDataWithBetType(Page<Bet> result) {
		Locale locale = new Locale("EN", "US");
		RuleBasedNumberFormat ruleBasedNumberFormat =
				new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
		result.forEach(bet -> {
			Integer size = bet.getBetSelections().size();
			String betTypeString = "";
			switch (size) {
				case 0: {
					break;
				}
				case 1: {
					betTypeString = messageSource
							.getMessage("SERVICE_CASINO_PROVIDER_INCENTIVE.BET_TYPE.SINGLE", null, locale);
					break;
				}
				case 2: {
					betTypeString = messageSource
							.getMessage("SERVICE_CASINO_PROVIDER_INCENTIVE.BET_TYPE.DOUBLE", null, locale);
					break;
				}
				case 3: {
					betTypeString = messageSource
							.getMessage("SERVICE_CASINO_PROVIDER_INCENTIVE.BET_TYPE.TRIPLE", null, locale);
					break;
				}
				default: {
					if (size != null && size > 0) {
						betTypeString = messageSource
								.getMessage(
									"SERVICE_CASINO_PROVIDER_INCENTIVE.BET_TYPE.FOLD",
										new Object[]{ StringUtils.capitalize(ruleBasedNumberFormat.format(size)) },
										locale);
					}
				}
			}
			bet.setBetType(betTypeString);
		});
	}

	private Specification<Bet> addToSpec(final List<String> list, Specification<Bet> spec, Function<List<String>, Specification<Bet>> predicateMethod) {
		if (list != null && !list.isEmpty()) {
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(list));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Bet> addToSpec(final String aString, Specification<Bet> spec, Function<String, Specification<Bet>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Bet> addToSpec(final Boolean aBoolean, Specification<Bet> spec, Function<Boolean, Specification<Bet>> predicateMethod) {
		if (aBoolean != null) {
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(aBoolean));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<Bet> addToSpec(final Date aDate, boolean addDay, Specification<Bet> spec, Function<Date, Specification<Bet>> predicateMethod) {
		if (aDate != null) {
			DateTime someDate = new DateTime(aDate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<Bet> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}
}
