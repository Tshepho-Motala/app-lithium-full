package lithium.service.cdn.provider.google.service.storage;

import lithium.service.cdn.provider.google.config.Status500ProviderNotConfiguredException;
import java.io.IOException;

public interface RequestInitializable {

  void initialize(String domain) throws Status500ProviderNotConfiguredException, IOException;
}
