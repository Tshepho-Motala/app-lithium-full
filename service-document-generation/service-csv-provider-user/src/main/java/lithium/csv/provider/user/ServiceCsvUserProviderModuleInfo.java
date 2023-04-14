package lithium.csv.provider.user;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCsvUserProviderModuleInfo extends ModuleInfoAdapter {
    public ServiceCsvUserProviderModuleInfo() {
        super();
    }
    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
    }
}
