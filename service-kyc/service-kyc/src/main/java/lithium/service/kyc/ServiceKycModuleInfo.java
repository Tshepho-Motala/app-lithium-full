package lithium.service.kyc;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ServiceKycModuleInfo extends ModuleInfoAdapter {
    public ServiceKycModuleInfo() {
        Role.Category kyc = Role.Category.builder().name("KYC").description("KYC Verifications").build();
        addRole(Role.builder().category(kyc).name("Player KYC verification Data View").role("PLAYER_KYC_RESULTS_VIEW").description("View KYC results history for a player.").build());

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/frontend/kyc/**").authenticated();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/kyc/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_KYC_RESULTS_VIEW')");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/kyc/verify").access("@lithiumSecurity.hasRoleInTree(authentication, 'NIN_PHONE_MANUAL_VERIFY')");
	    http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/kyc/banks").authenticated();
    }
}
