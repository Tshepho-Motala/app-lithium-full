package lithium.service.migration.service;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import org.springframework.stereotype.Service;

@Service
public class SimpleQueryExecutorService {
  private final BigQuery bigQuery;

  public SimpleQueryExecutorService(BigQuery bigQuery) {
    this.bigQuery = bigQuery;
  }

  public TableResult execute(String query) throws InterruptedException {
    QueryJobConfiguration config =
        QueryJobConfiguration.newBuilder(query)
            .setUseLegacySql(false)
            .build();

    return bigQuery.query(config);
  }
}
