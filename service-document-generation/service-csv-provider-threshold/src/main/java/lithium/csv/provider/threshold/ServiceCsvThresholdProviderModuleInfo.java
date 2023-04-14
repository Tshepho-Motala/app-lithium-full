package lithium.csv.provider.threshold;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCsvThresholdProviderModuleInfo extends ModuleInfoAdapter {

  public ServiceCsvThresholdProviderModuleInfo() {
    super();
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);
  }
}
