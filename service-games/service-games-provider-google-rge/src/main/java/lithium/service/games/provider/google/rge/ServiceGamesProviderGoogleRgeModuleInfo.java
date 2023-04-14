package lithium.service.games.provider.google.rge;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.games.provider.google.rge.configs.ProviderConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ServiceGamesProviderGoogleRgeModuleInfo extends ModuleInfoAdapter {
    ServiceGamesProviderGoogleRgeModuleInfo() {
        super();
        List<ProviderConfigProperty> properties = new ArrayList<>();

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.TYPE.getName())
                        .tooltip("Type - Google credentials ")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PROJECT_ID.getName())
                        .tooltip("Project ID - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PRIVATE_KEY_ID.getName())
                        .tooltip("Private Key ID - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PRIVATE_KEY.getName())
                        .tooltip("Private Key - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.CLIENT_EMAIL.getName())
                        .tooltip("Client Email - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.CLIENT_ID.getName())
                        .tooltip("Client ID - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.AUTH_URI.getName())
                        .tooltip("Auth URI - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.TOKEN_URI.getName())
                        .tooltip("Token URI - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.AUTH_PROVIDER_X509_CERT_URL.getName())
                        .tooltip("Auth Provider X509 Certificate URL - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.CLIENT_X509_CERT_URL.getName())
                        .tooltip("Client X509 Certificate URL - Google credentials")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.BUCKET_NAME.getName())
                        .tooltip("Bucket Name - Google storage")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PREDICT_URL.getName())
                        .tooltip("Predict Url - Google RGE")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PROJECT.getName())
                        .tooltip("Project - Google RGE")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.LOCATION.getName())
                        .tooltip("Location - Google RGE")
                        .dataType(String.class)
                        .build()

        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.ENDPOINT.getName())
                        .tooltip("Endpoint - Google RGE")
                        .dataType(String.class)
                        .build()

        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigProperties.PAGE_SIZE.getName())
                        .tooltip("Page Size - Number of recommended games to be returned on the response; default 12 if empty")
                        .dataType(String.class)
                        .build()

        );

        ProviderConfig providerConfig = ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.GAMES)
                .properties(properties)
                .build();

        //Add the provider to moduleinfo
        addProvider(providerConfig);

        roles();
    }

    private void roles() {

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }

}
