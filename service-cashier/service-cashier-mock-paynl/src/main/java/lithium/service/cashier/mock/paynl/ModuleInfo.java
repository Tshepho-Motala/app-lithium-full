package lithium.service.cashier.mock.paynl;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleInfo extends ModuleInfoAdapter {
    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/transactions/**").permitAll();
    }
}
