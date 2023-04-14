package lithium.service.accounting.provider.internal.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.enums.IngestionAccountCode;
import lithium.service.accounting.enums.TransactionTypeCode;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.BalanceMigrationHistoricDetails;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialBalanceIngestionService {

  private static final String PLAYER_BALANCE = "PLAYER_BALANCE";
  private static final String SYSTEM_GUID = "system";

  private final CachingDomainClientService cachingDomainClientService;
  private final AccountService accountService;
  private final TransactionTypeService transactionTypeService;
  private final TransactionServiceWrapper transactionServiceWrapper;


  public void initiatePhase1Ingestion(BalanceMigrationHistoricDetails details)
      throws Status500InternalServerErrorException {
    SW.start("Phase1 Account Balance adjustment");

    String domainCurrency = cachingDomainClientService.getDefaultDomainCurrency(
        details.getDomainName());
    String[] labels = new String[]{details.getCustomerId() + "-credit-1-1"};

    findOrCreateAccounts(details, domainCurrency);

    transactionServiceWrapper.adjustMulti(details.getOpeningBalancePhase1(),
        DateTime.now(DateTimeZone.UTC), PLAYER_BALANCE, PLAYER_BALANCE,
        TransactionTypeCode.OPERATOR_MIGRATION_CREDIT.getName(),
        IngestionAccountCode.OPERATOR_MIGRATION_OPENING_CREDIT.getName(),
        "OPERATOR_MIGRATION", labels, domainCurrency,
        details.getDomainName(), details.getUserGuid(), SYSTEM_GUID,
        false, null, new TransactionStreamData(), null,
        null, new BalanceAdjustEvent(), false);
    SW.stop();
  }

  private void findOrCreateAccounts(BalanceMigrationHistoricDetails details, String code) {
    TransactionType transactionType = transactionTypeService.findOrCreate(
        TransactionTypeCode.OPERATOR_MIGRATION_CREDIT.getName());

    accountService.findOrCreate(PLAYER_BALANCE,
        PLAYER_BALANCE,
        code, details.getDomainName(), details.getUserGuid(),
        transactionType);
    accountService.findOrCreate(
        IngestionAccountCode.OPERATOR_MIGRATION_OPENING_CREDIT.getName(),
        "OPERATOR_MIGRATION",
        code, details.getDomainName(), details.getUserGuid(),
        transactionType);
  }

  @TimeThisMethod
  public void initiatePhase2Ingestion(BalanceMigrationHistoricDetails details)
      throws Status500InternalServerErrorException {
    SW.start("Phase2 Account Balance adjustment");

    String domainCurrency = cachingDomainClientService.getDefaultDomainCurrency(
        details.getDomainName());

    Account account = accountService.find(PLAYER_BALANCE, PLAYER_BALANCE,
        domainCurrency, details.getDomainName(), details.getUserGuid());

    if (ObjectUtils.isEmpty(account)) {
      throw new Status411AccountingUserNotFoundException(details.getUserGuid());
    }
    // FIXME: 2023/02/01 PLAT-11876 Till we can get the phase one opening balance we cant do much here
    if ((-1 * account.getBalanceCents()) != details.getOpeningBalancePhase1()) {
      throw new Status414AccountingTransactionDataValidationException(
          "Phase 1 balance does not align with initially stored balance");
    }

    String[] debitLabels = new String[]{details.getCustomerId() + "-debit-2-1"};
    String[] creditLabels = new String[]{details.getCustomerId() + "-credit-2-1"};
    DateTime now = DateTime.now(DateTimeZone.UTC);

    transactionServiceWrapper.adjustMulti(account.getBalanceCents(), now,
        PLAYER_BALANCE, PLAYER_BALANCE,
        TransactionTypeCode.OPERATOR_MIGRATION_DEBIT.getName(),
        IngestionAccountCode.OPERATOR_MIGRATION_OPENING_DEBIT.getName(),
        "OPERATOR_MIGRATION", debitLabels, domainCurrency,
        details.getDomainName(), details.getUserGuid(), SYSTEM_GUID,
        false, null, new TransactionStreamData(), null,
        null, new BalanceAdjustEvent(), false);

    transactionServiceWrapper.adjustMulti(details.getOpeningBalancePhase2(), now,
        PLAYER_BALANCE, PLAYER_BALANCE,
        TransactionTypeCode.OPERATOR_MIGRATION_CREDIT.getName(),
        IngestionAccountCode.OPERATOR_MIGRATION_OPENING_CREDIT.getName(),
        "OPERATOR_MIGRATION", creditLabels, domainCurrency,
        details.getDomainName(), details.getUserGuid(), SYSTEM_GUID,
        false, null, new TransactionStreamData(), null,
        null, new BalanceAdjustEvent(), false);
    SW.stop();
  }

}
