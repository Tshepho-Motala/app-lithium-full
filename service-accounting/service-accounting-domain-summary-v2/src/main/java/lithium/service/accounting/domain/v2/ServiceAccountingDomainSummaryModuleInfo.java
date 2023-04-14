package lithium.service.accounting.domain.v2;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

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
