/*
package lithium.service.user.service.bigQueryExecutor;

import static org.mockito.Mockito.when;

import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryExecutorServiceImplTest extends TestCase {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

 private QueryExecutorService queryExecutorServiceInterface;
 private QueryExecutorServiceImpl queryExecutorService;


 @Before
 public void setup() throws InterruptedException {

 queryExecutorServiceInterface = (QueryExecutorService) queryExecutorServiceInterface.runQuery("");
 queryExecutorServiceInterface.runAndLogQuery("");
 queryExecutorServiceInterface.getTotalRows("SELECT * FROM `lithium-virginbet-sandbox.dk_dwh.Dim_Customers` ");
 queryExecutorService = new QueryExecutorServiceImpl();

 }

 @Test
 public void testRunQuery() throws InterruptedException{

   String query = "Test Query";

   QueryExecutorServiceImpl queryExecutorService =  new QueryExecutorServiceImpl();
   BigQuery bigquery = Mockito.mock(BigQuery.class);
   queryExecutorService.setBigquery(bigquery);
   QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
   Page<FieldValueList> page = new Page<FieldValueList>() {
     @Override
     public boolean hasNextPage() {
       return false;
     }
     @Override
     public String getNextPageToken() {
       return null;
     }
     @Override
     public Page<FieldValueList> getNextPage() {
       return null;
     }
     @Override
     public Iterable<FieldValueList> iterateAll() {
       return null;
     }
     @Override
     public Iterable<FieldValueList> getValues() {
       return null;
     }
   };
   List<FieldValueList> results = new ArrayList<>();
   TableResult result = new TableResult(null, 5, page);
   when(bigquery.query(queryConfig)).thenReturn(result);
   assertEquals(result.iterateAll(), queryExecutorService.runQuery(query));

 }


  @Test
  public void testGetTotalRows() throws InterruptedException{

   String query = "Test Query";

    QueryExecutorServiceImpl queryExecutorService =  new QueryExecutorServiceImpl();

    BigQuery bigquery = Mockito.mock(BigQuery.class);

    queryExecutorService.setBigquery(bigquery);


    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(query).build();

    Page<FieldValueList> page = new Page<FieldValueList>() {
      @Override
      public boolean hasNextPage() {
        return false;
      }
      @Override
      public String getNextPageToken() {
        return null;
      }
      @Override
      public Page<FieldValueList> getNextPage() {
        return null;
      }
      @Override
      public Iterable<FieldValueList> iterateAll() {
        return null;
      }
      @Override
      public Iterable<FieldValueList> getValues() {
        return null;
      }
    };
    TableResult results = new TableResult(null, 5, page);
    when(bigquery.query(queryConfig)).thenReturn(results);
    assertEquals(5, queryExecutorService.getTotalRows(query));

  }



}
*/
