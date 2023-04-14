package lithium.service.document.provider;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.document.provider.config.ProviderConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Slf4j
@RestController
public class ServiceDocumentHelloSodaModuleInfo extends ModuleInfoAdapter {
    public ServiceDocumentHelloSodaModuleInfo() {
        super();
        ArrayList<ProviderConfigProperty> properties = new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PROFILE_API_URL.getValue())
                .required(true)
                .tooltip("Profile API URL used to create a job and receive information about it")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PROFILE_API_V1_URL.getValue())
                .required(true)
                .tooltip("Profile API V1 URL used to create a job")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.I_DOCUFY_API_URL.getValue())
                .required(true)
                .tooltip("IDocufy API URL used to store user data and photos")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PRODUCT_ID.getValue())
                .required(true)
                .tooltip("Product id used to identify company in hello soda side, provides by HelloSoda")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PROFILE_BEARER.getValue())
                .required(true)
                .tooltip("Bearer used to communicate with profile api")
                .dataType(String.class)
                .version(1)
                .build());

        //Add the provider to moduleinfo
        addProvider(ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.DOCUMENT)
                .properties(properties)
                .build());
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/frontend/api/submitJob/**").authenticated();
        http.authorizeRequests().antMatchers("/frontend/api/changelogs/**").authenticated();
        http.authorizeRequests().antMatchers("/webhook/notify").permitAll();
        http.authorizeRequests().antMatchers("/webhook/facebook/notify").permitAll();

    }
}
