package lithium.service.migration.factory;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.models.enities.Progress;

public interface MigrationIngestion {

  MigrationType getType();

  String initiateMigration(TableInfo bigqueryTable, TableId tableId, Progress currentProgress, TableResult results, FieldList fields)
      throws LithiumServiceClientFactoryException, Status500InternalServerErrorException;
}
