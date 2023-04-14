package lithium.service.cashier.mock.smartcash.configuration;

import lithium.service.cashier.mock.smartcash.filters.FilterChainExceptionHandler;
import lithium.service.cashier.mock.smartcash.filters.SmartcashAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(-100500)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    FilterChainExceptionHandler filterChainExceptionHandler;

    @Autowired
    SmartcashAuthenticationFilter smartcashAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().addFilterAfter(smartcashAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(filterChainExceptionHandler, SmartcashAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/auth/oauth2/token").permitAll()
            .antMatchers("/health").permitAll()
            .antMatchers("/prometheus").permitAll()
            .antMatchers("/**").authenticated();
    }
}
