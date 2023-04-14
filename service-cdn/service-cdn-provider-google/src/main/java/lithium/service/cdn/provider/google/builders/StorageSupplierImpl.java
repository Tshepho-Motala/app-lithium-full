package lithium.service.cdn.provider.google.builders;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lithium.service.cdn.provider.google.config.ProviderConfig;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 *
 */
@Component
public class StorageSupplierImpl implements StorageSupplier {

  /**
   * @param config
   * @return
   */
  public Storage get(ProviderConfig config) throws IOException {
    //Create Google Storage object from Google Credentials required for GCP
    return StorageOptions.newBuilder()
        .setCredentials(CredentialsBuilder.build(config))
        .build()
        .getService();
  }
}
