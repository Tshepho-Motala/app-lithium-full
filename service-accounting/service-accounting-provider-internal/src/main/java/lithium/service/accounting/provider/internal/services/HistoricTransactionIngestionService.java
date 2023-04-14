package lithium.service.accounting.provider.internal.services;

import java.util.ArrayList;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.AccountMigrationHistoricDetails;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricTransactionIngestionService {
  @Autowired
  HistoricTransactionIngestionService self;
  private final SummaryAccountService summaryAccountService;
  private final AccountCodeService accountCodeService;
  private final AccountTypeService accountTypeService;

  private final SummaryAccountTransactionTypeService summaryAccountTransactionTypeService;
  private final AccountService accountService;
  private final CachingDomainClientService cachingDomainClientService;
  private final TransactionTypeService transactionTypeService;

  public void startupCodes() {
    List<String> codes = new ArrayList<>();

    codes.add("SPORTS_WIN");
    codes.add("SPORTS_BET");
    codes.add("PLAYER_BALANCE");
    codes.add("CASINO_WIN");
    codes.add("CASINO_BET");
    codes.add("CASHIER_DEPOSIT");

    for (String code: codes){
      TransactionType transactionType = transactionTypeService.findOrCreate(code);
      String contraAccountCodeValue = code + "_OPERATOR_MIGRATION";
      accountCodeService.findOrCreate(contraAccountCodeValue);
      accountTypeService.findOrCreate(code, transactionType);
    }

    initiateBalanceCodes();
  }

  public void initiateBalanceCodes() {
    Arrays.stream(lithium.service.accounting.enums
        .IngestionAccountCode.values()).forEach(accountCode -> {
      accountCodeService.findOrCreate(accountCode.getName());
      transactionTypeService.findOrCreate(accountCode.getTransactionTypeCode().getName());
    });
  }

  @TimeThisMethod
  public void initiateIngestion(AccountMigrationHistoricDetails accountMigrationHistoricDetails) {

      SW.start("Account Ingestion for customer: " + accountMigrationHistoricDetails.getCustomerId());

      accountMigrationHistoricDetails.setUserGuid(accountMigrationHistoricDetails.getUserGuid());
      String domainName = accountMigrationHistoricDetails.getUserGuid().split("/")[0];
      String currencyCode = cachingDomainClientService.getDefaultDomainCurrency(domainName);
      TransactionType transactionType = transactionTypeService.findOrCreate(
          accountMigrationHistoricDetails
              .getTransactionTypeCode());

      String contraAccountCodeValue = transactionType.getCode() + "_OPERATOR_MIGRATION";
      Account account = accountService.findOrCreate(lithium.service.accounting.enums
              .IngestionAccountCode.PLAYER_BALANCE_OPERATOR_MIGRATION.getName(),
          accountMigrationHistoricDetails.getTransactionTypeCode(),
          currencyCode, domainName, accountMigrationHistoricDetails.getUserGuid(),
          transactionType);
      Account contraAccount = accountService.findOrCreate(contraAccountCodeValue,
          accountMigrationHistoricDetails
              .getTransactionTypeCode(),
          currencyCode, domainName, accountMigrationHistoricDetails.getUserGuid(),
          transactionType);

      Date date = Date.from(accountMigrationHistoricDetails.getCreatedOn().atStartOfDay()
          .toInstant(ZoneOffset.UTC));

      summaryAccountService.populateAccount(account.getDomain(), DateUtil.toDateTime(accountMigrationHistoricDetails.getCreatedOn().atStartOfDay()), account, transactionType);
      summaryAccountService.populateAccount(contraAccount.getDomain(), DateUtil.toDateTime(accountMigrationHistoricDetails.getCreatedOn().atStartOfDay()), contraAccount, transactionType);

     self.initiateIngestion(account, contraAccount, date,
         accountMigrationHistoricDetails.getEntryAmountCents(), transactionType);
      SW.stop();
  }


  @Transactional(rollbackFor = java.lang.Exception.class, propagation = Propagation.REQUIRED)
  public void initiateIngestion(Account account, Account contraAccount, Date date, Long amountCents, TransactionType transactionType) {
    summaryAccountService.adjust(TransactionEntry.builder()
        .account(account)
        .date(date)
        .amountCents(amountCents)
        .build());

    summaryAccountService.adjust(TransactionEntry.builder()
        .account(contraAccount)
        .date(date)
        .amountCents(amountCents)
        .build());

    summaryAccountTransactionTypeService.adjust(transactionType, account,
        amountCents, date);
  }
}