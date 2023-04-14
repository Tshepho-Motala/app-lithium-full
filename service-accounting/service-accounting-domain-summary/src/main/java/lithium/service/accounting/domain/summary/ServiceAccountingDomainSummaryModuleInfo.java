package lithium.service.accounting.domain.summary;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lombok.Getter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Component
public class ServiceAccountingDomainSummaryModuleInfo extends ModuleInfoAdapter {
    public ServiceAccountingDomainSummaryModuleInfo() {
        super();
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
            .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
            .antMatchers("/backoffice/summary/*/{domainName}/**").access(
                    "@lithiumSecurity.hasAllRolesForDomain(authentication, #domainName, 'DASHBOARD', 'ACCOUNTING_SUMMARY_DOMAIN', 'BETA')");
    }
}
