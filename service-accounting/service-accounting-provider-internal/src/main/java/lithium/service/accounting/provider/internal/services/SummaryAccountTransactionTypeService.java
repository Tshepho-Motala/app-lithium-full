package lithium.service.accounting.provider.internal.services;

import static lithium.service.accounting.provider.internal.services.TransactionEntryService.PLAYER_BALANCE_TYPE_CODE_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountTransactionTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SummaryAccountTransactionTypeService {
	public static final List<String> EXCLUDED_TRANSACTION_TYPES = Arrays.asList("SPORTS_RESERVE", "SPORTS_RESERVE_RETURN", "SPORTS_RESERVE_CANCEL");

	@Autowired PeriodService periodService;
	@Autowired SummaryAccountTransactionTypeRepository repository;
	@Autowired LithiumMetricsService metrics;
	@Autowired RabbitEventService rabbitEventService;
	@Autowired ModelMapper modelMapper;
	@Autowired PlayerService playerService;
	@Autowired Properties properties;

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.MANDATORY)
	@TimeThisMethod
	public void adjust(TransactionEntry e, TransactionType tt) {
		List<CompleteSummaryAccountTransactionTypeDetail> completeSummaryAccountTransactionTypeDetails = new ArrayList<>();
		Long debitCents = (e.getAmountCents() > 0) ? e.getAmountCents(): 0;
		Long creditCents = (e.getAmountCents() > 0) ? 0: e.getAmountCents() * -1;

		for (int granularity = 1; granularity <= 5; granularity++) {
			SW.start("findOrCreatePeriod " + granularity);
			Period period = periodService.findOrCreatePeriod(new DateTime(e.getDate().getTime()), e.getAccount().getDomain(), granularity);
			SW.stop();

			SummaryAccountTransactionType account = findOrCreate(period, e.getAccount(), tt, true);
			account.setDebitCents(account.getDebitCents() + debitCents);
			account.setCreditCents(account.getCreditCents() + creditCents);
			account.setTranCount(account.getTranCount() + 1);
			account = repository.save(account);

			if (properties.getBalanceAdjustments().isSendCompletedSummaryAccountTransactionTypeEvent()) {
				completeSummaryAccountTransactionTypeDetails.add(
						CompleteSummaryAccountTransactionTypeDetail.builder()
								.summaryAccountTransactionType(modelMapper.map(account,
										lithium.service.accounting.objects.SummaryAccountTransactionType.class))
								.granularity(granularity)
								.build()
				);
			}
		}

		if (properties.getBalanceAdjustments().isSendCompletedSummaryAccountTransactionTypeEvent()) {
			rabbitEventService.sendCompletedSummaryAccountTransactionTypeEvent(CompleteSummaryAccountTransactionType
					.builder()
					.createdOn(e.getDate().toString())
					.transactionType(tt.getCode())
					.details(completeSummaryAccountTransactionTypeDetails)
					.build());
		}
	}
	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.MANDATORY)
	@TimeThisMethod
	public void adjust(TransactionType transactionType, Account account, Long amountCents, Date date){
		Long debitCents = (amountCents > 0) ? amountCents: 0;
		Long creditCents = (amountCents > 0) ? 0: (amountCents * -1);

		for (int granularity = 1; granularity <= Period.GRANULARITY_TOTAL; granularity++) {
			SW.start("findOrCreatePeriod " + granularity);
			Period period = periodService.findOrCreatePeriod(new DateTime(date.getTime()), account.getDomain(), granularity);
			SW.stop();

			SummaryAccountTransactionType accountTransactionType = findOrCreate(period, account, transactionType, true);
			accountTransactionType.setDebitCents(accountTransactionType.getDebitCents() + debitCents);
			accountTransactionType.setCreditCents(accountTransactionType.getCreditCents() + creditCents);
			accountTransactionType.setTranCount(accountTransactionType.getTranCount() + 1);
			repository.save(accountTransactionType);
		}

	}
	
	public SummaryAccountTransactionType findOrCreate(Period period, Account account, TransactionType transactionType, 
			boolean logErrorOnCreate) {
		
		SummaryAccountTransactionType existing = repository.findByPeriodAndAccountAndTransactionType(period, account, transactionType); 
		
		if (existing != null) return existing;
		
		if (logErrorOnCreate) {
			log.info("Creating missing summaryaccounttransactiontype period {} owner {} trantype {} ",
					period.getGranularity(), account.getOwner().getGuid(), transactionType.getClass());
		}
		
		SummaryAccountTransactionType summaryAccountTransactionType = SummaryAccountTransactionType.builder()
				.account(account)
				.period(period)
				.transactionType(transactionType)
				.debitCents(0L).creditCents(0L)
				.damaged(true)
				.tranCount(0L)
				.build();			

		return repository.save(summaryAccountTransactionType);		
	}

	public List<lithium.service.accounting.objects.TransactionType> findUserBalanceMovementTypes(String userGuid) {
        return repository.findDistinctTransactionTypesUsedByUserByAccountCode(userGuid, PLAYER_BALANCE_TYPE_CODE_NAME)
                .stream()
                .filter(transactionType -> !EXCLUDED_TRANSACTION_TYPES.contains(transactionType.getCode()))
                .collect(Collectors.toList());
	}
}
