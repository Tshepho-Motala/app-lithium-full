package lithium.service.cdn.provider.google.config;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class ProviderConfigService {

  @Autowired
  LithiumServiceClientFactory services;

  /**
   * @param providerName
   * @param domainName
   * @return
   * @throws Status500ProviderNotConfiguredException
   */
  public ProviderConfig getConfig(String providerName, String domainName) throws Status500ProviderNotConfiguredException {
    ProviderClient cl = getProviderService();
    Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

    if (!pp.isSuccessful() || pp.getData() == null) {
      throw new Status500ProviderNotConfiguredException();
    }

    ProviderConfig config = new ProviderConfig();
    for (ProviderProperty p : pp.getData()) {
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET.getValue())) {
        config.setBucket(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET_PREFIX.getValue())) {
        config.setBucketPrefix(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET_IMAGE_PREFIX.getValue())) {
        config.setBucketImagePrefix(p.getValue());
      }

      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET_CMS_IMAGE_PREFIX.getValue())) {
        config.setBucketCmsImagePrefix(p.getValue());
      }

      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET_CMS_ASSET_PREFIX.getValue())) {
        config.setBucketCmsAssetPrefix(p.getValue());
      }

      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CACHE_LENGTH.getValue())) {
        config.setCacheLength(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.URI.getValue())) {
        config.setUri(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.TYPE.getValue())) {
        config.setType(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PROJECT_ID.getValue())) {
        config.setProjectId(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PRIVATE_KEY_ID.getValue())) {
        config.setPrivateKeyId(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PRIVATE_KEY.getValue())) {
        config.setPrivateKey(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CLIENT_EMAIL.getValue())) {
        config.setClientEmail(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CLIENT_ID.getValue())) {
        config.setClientId(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.AUTH_URI.getValue())) {
        config.setAuthUri(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.TOKEN_URI.getValue())) {
        config.setTokenUri(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.AUTH_PROVIDER_CERT_URL.getValue())) {
        config.setAuthProviderCertUrl(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CLIENT_CERT_URL.getValue())) {
        config.setClientCertUrl(p.getValue());
      }
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BUCKET_IMAGE_PREFIX.getValue())) {
        config.setClientCertUrl(p.getValue());
      }

    }

    return config;
  }

  /**
   * @return
   */
  private ProviderClient getProviderService() {
    ProviderClient cl = null;

    try {
      cl = services.target(ProviderClient.class, "service-domain", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting provider properties", e);
    }

    return cl;
  }
}
