package lithium.csv.cashier.transactions.provider;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCsvTransactionsProviderModuleInfo extends ModuleInfoAdapter {

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
    }
}
