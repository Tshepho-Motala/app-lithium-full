package lithium.service.cdn.provider.google.builders;

import com.google.cloud.storage.Storage;
import lithium.service.cdn.provider.google.config.ProviderConfig;

import java.io.IOException;

public interface StorageSupplier {

  Storage get(ProviderConfig config) throws IOException;

}
