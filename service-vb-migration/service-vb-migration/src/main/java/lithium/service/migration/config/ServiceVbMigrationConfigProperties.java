package lithium.service.migration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lithium.service.migration")
@Data
public class ServiceVbMigrationConfigProperties {

  private Query query = new Query();
  private PendingSelfExclusionJob pendingSelfExclusionJob = new PendingSelfExclusionJob();
  private HistoricIngestion historicIngestion = new HistoricIngestion();

  @Data
  public static class Query {
    private String dataSet;
    private String users;
    private Integer batchSize;
    private Integer accountsBatchSize;
    private String accounts;
    private String transactionTypes;
    private String openingBalancePhase1;
    private String openingBalancePhase2;
    private String usersCasinoMigration;
    private String gamesCasinoMigration;
    private String dataCasinoMigration;
    private String cashierPaymentMethods;
    private String cashierTransactions;
    private String cashierUserVerifyTransactionsExist;
    private String cashierUserVerifyTransactionsExistWithProvider;
    private String playerLimitPreferences;
    private String realityCheckMigration;

    public String getCashierUserVerifyTransactionsExistWithProvider() {
      return null;
    }

    public String getCashierUserVerifyTransactionsExist() {
      return null;
    }
  }

  @Data
  public static class PendingSelfExclusionJob {
    private String cron;
    private int pageSize;
  }

  @Data
  public static class HistoricIngestion{
    private String domain;
    private String legacyGuid;
    private String domainCurrencyCode;
    private int dlqRetries;
  }

}
