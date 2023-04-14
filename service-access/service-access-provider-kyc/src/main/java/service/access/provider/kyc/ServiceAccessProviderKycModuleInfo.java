package service.access.provider.kyc;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
public class ServiceAccessProviderKycModuleInfo extends ModuleInfoAdapter {

	ServiceAccessProviderKycModuleInfo() {
        super();
        ArrayList<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();
        addProvider(
                ProviderConfig.builder()
                        .name(getModuleName())
                        .type(ProviderType.ACCESS)
                        .properties(properties)
                        .build()
        );
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}
