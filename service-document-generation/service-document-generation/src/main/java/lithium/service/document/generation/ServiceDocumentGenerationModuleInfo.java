package lithium.service.document.generation;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceDocumentGenerationModuleInfo extends ModuleInfoAdapter {

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
                .antMatchers("/document/{id}/**").permitAll()
                .antMatchers("/document/**").authenticated()
                .antMatchers("/processing/**").authenticated();
        // @formatter:on
    }
}
