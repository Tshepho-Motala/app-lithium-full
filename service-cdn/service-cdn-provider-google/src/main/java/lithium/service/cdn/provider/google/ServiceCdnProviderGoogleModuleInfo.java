package lithium.service.cdn.provider.google;

import java.util.ArrayList;
import java.util.List;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.cdn.provider.google.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class ServiceCdnProviderGoogleModuleInfo extends ModuleInfoAdapter {

  /**
   *
   */
  ServiceCdnProviderGoogleModuleInfo() {
    super();
    //Arraylist containing all the relevant properties for the provider
    List<ProviderConfigProperty> properties = new ArrayList<>();

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BUCKET.getValue())
            .required(Boolean.TRUE)
            .tooltip("The name of the bucket on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BUCKET_PREFIX.getValue())
            .required(Boolean.FALSE)
            .tooltip("The folder location for all Templates to be stored.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BUCKET_CMS_IMAGE_PREFIX.getValue())
            .required(Boolean.FALSE)
            .tooltip("The folder location for all CMS Images to be stored.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BUCKET_CMS_ASSET_PREFIX.getValue())
            .required(Boolean.FALSE)
            .tooltip("The folder location for all CMS assets to be stored. (fonts,css, etc)")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BUCKET_IMAGE_PREFIX.getValue())
            .required(Boolean.FALSE)
            .tooltip("The folder location for all Images to be stored.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.CACHE_LENGTH.getValue())
            .required(Boolean.FALSE)
            .tooltip("Indicate the cache length for content in seconds.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.URI.getValue())
            .required(Boolean.TRUE)
            .tooltip("The uri of the bucket on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.TYPE.getValue())
            .required(Boolean.TRUE)
            .tooltip("What type of account this is on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.PROJECT_ID.getValue())
            .required(Boolean.TRUE)
            .tooltip("The ID of the project on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.PRIVATE_KEY_ID.getValue())
            .required(Boolean.TRUE)
            .tooltip("The ID of the private key on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.PRIVATE_KEY.getValue())
            .required(Boolean.TRUE)
            .tooltip("The private key on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.CLIENT_EMAIL.getValue())
            .required(Boolean.TRUE)
            .tooltip("The email address of the client on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.CLIENT_ID.getValue())
            .required(Boolean.TRUE)
            .tooltip("The ID of the client on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.AUTH_URI.getValue())
            .required(Boolean.TRUE)
            .tooltip("The URI for authentication on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.TOKEN_URI.getValue())
            .required(Boolean.TRUE)
            .tooltip("The URI for tokens on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.AUTH_PROVIDER_CERT_URL.getValue())
            .required(Boolean.TRUE)
            .tooltip("The URL for authentication provider certificates on GCP.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.CLIENT_CERT_URL.getValue())
            .required(Boolean.TRUE)
            .tooltip("The URL for client certificates on GCP.")
            .dataType(String.class)
            .build()
    );

    ProviderConfig providerConfig = ProviderConfig.builder()
        .name(getModuleName())
        .type(ProviderConfig.ProviderType.CDN)
        .properties(properties)
        .build();

    //Add the provider to moduleinfo
    addProvider(providerConfig);

    roles();
  }

  /**
   *
   */
  private void roles() {
  }

  /**
   * @param http
   * @throws Exception
   */
  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);

    http.authorizeRequests().antMatchers("/backoffice/{domainName}/cms-image/**")
        .access("@lithiumSecurity.hasDomainRole(authentication,#domainName, 'GAME_TILE_PUBLISH','BANNER_IMAGE_PUBLISH')");

    http.authorizeRequests().antMatchers("/backoffice/{domainName}/cms-asset/**")
        .access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'WEB_ASSET_PUBLISH')");

    http.authorizeRequests().antMatchers("/backoffice/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'TEMPLATES_PUBLISH')");
    http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
  }
}
