package lithium.service.leader;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceLeaderModuleInfo extends ModuleInfoAdapter {
    public ServiceLeaderModuleInfo() {
        super();
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}
