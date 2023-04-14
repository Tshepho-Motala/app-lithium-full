package lithium.jpa.dialect;

import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.MySQLStorageEngine;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Copypaste the original @org.hibernate.dialect.MariaDBDialect class
 * The only difference is that the original extends the MySQL5Dialect and its using leads to the errors like:  Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: wrong column type encountered in column [value] in table [`label_value`]; found [longtext (Types#LONGVARCHAR)], but expecting [varchar(2048) (Types#VARCHAR)]
 * We need this custom dialect to override the default for hibernate starting v.5.5.3 unquotedCaseStrategy=UPPER (while the previous default value was MIXED),
 * as the UPPER strategy transforms catalog names to uppercase when querying and causing org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [xxx]
 * Google cloud SQL doesn't return the proper values for building unquotedCaseStrategy(see @org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder), so the default value is used.
 * Pay attention, locally run from docker MySQL 5.7 returns the correct unquotedCaseStrategy, so the issue can't be reproduced on it.
 */
public class CustomMariaDBDialect extends MySQLDialect {

  public CustomMariaDBDialect() {
    super();
  }

  public boolean supportsRowValueConstructorSyntaxInInList() {
    return true;
  }

  @Override
  protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
    return InnoDBStorageEngine.INSTANCE;
  }

  @Override
  public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData)
      throws SQLException {

    // some MariaDB drivers does not return case strategy info
    builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.MIXED);
    builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);

    return super.buildIdentifierHelper(builder, dbMetaData);
  }
}
