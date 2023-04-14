package lithium.service.cashier.mock.inpay;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleInfo extends ModuleInfoAdapter {
    private static final int ACCESS_OVERRIDE_ORDER = SecurityProperties.BASIC_AUTH_ORDER - 2;

    @Configuration
    @Order(ACCESS_OVERRIDE_ORDER)
    protected static class UISecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void init(WebSecurity web) {
            web.ignoring().antMatchers(
                    "/transactions/**"
            ).antMatchers();
        }
    }

}
