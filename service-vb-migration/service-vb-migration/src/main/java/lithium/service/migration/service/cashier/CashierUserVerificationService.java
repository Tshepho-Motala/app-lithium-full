package lithium.service.migration.service.cashier;

import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.service.SimpleQueryExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CashierUserVerificationService {
  private final ServiceVbMigrationConfigProperties config;
  private final SimpleQueryExecutorService executor;

  @Autowired
  public CashierUserVerificationService(ServiceVbMigrationConfigProperties config, SimpleQueryExecutorService executor) {
    this.config = config;
    this.executor = executor;
  }

  public boolean hasUserMadeCashierTransactions(String customerId) throws Status500InternalServerErrorException {
    String query = config.getQuery().getCashierUserVerifyTransactionsExist();
    query = query.replaceAll("#customerId", customerId);

    String logInfo = "customer id: " + customerId + ", final query: " + query;

    return execute(logInfo, "player cashier transaction verification", query);
  }

  public boolean hasUserMadeCashierTransactionsUsingProvider(String customerId, String providerName)
      throws Status500InternalServerErrorException {
    String query = config.getQuery().getCashierUserVerifyTransactionsExistWithProvider();
    query = query.replaceAll("#customerId", customerId);
    query = query.replaceAll("#providerName", providerName);

    String logInfo = "customer id: " + customerId + ", provider name: " + providerName + ", final query: " + query;

    return execute(logInfo, "player cashier transaction provider verification", query);
  }

  private boolean execute(String logInfo, String queryIdentifier, String query) throws Status500InternalServerErrorException {
    log.trace("Constructed query for {} | {}", queryIdentifier, logInfo);

    try {
      TableResult result = executor.execute(query);

      if (result.getTotalRows() != 1) {
        return false;
      }

      return true;
    } catch (InterruptedException e) {
      String errorMessage = "Unable to execute query on BQ";
      log.error(errorMessage + " | " + logInfo + " | {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(errorMessage);
    }
  }
}
