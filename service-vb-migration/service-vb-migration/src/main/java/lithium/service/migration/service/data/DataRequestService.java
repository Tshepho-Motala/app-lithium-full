package lithium.service.migration.service.data;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import lithium.service.libraryvbmigration.data.dto.MigrationPlayerBasic;

public interface DataRequestService {

  MigrationPlayerBasic getPlayerBasic(FieldValueList fieldValues, FieldList fields);
  MigrationCredential generateCredentials(FieldValueList fieldValues, FieldList fields);
}

