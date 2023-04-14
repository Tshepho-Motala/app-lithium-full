package lithium.service.reward.provider.casino.blueprint.config;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderConfigService {

  private final LithiumServiceClientFactory services;

  public ProviderConfig getConfig(String providerName, String domainName)
  throws Exception
  {
    ProviderClient cl = getProviderService();
    if (cl == null) {
      throw new Exception(domainName);
    }

    Response<Provider> provider = cl.findByUrlAndDomainName(providerName, domainName);
    if (!provider.isSuccessful() || provider.getData() == null) {
      throw new Exception(domainName);
    }

    if (!provider.getData().getEnabled()) {
      throw new Status512ProviderNotConfiguredException(String.format("%s is not configured/enabled for domain %s",providerName, domainName));
    }

    Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

    if (!pp.isSuccessful() || pp.getData() == null) {
      throw new Exception(domainName);
    }

    ProviderConfig config = new ProviderConfig();
    for (ProviderProperty p: pp.getData()) {
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_BASE_URL.value())) {
        config.setRewardsBaseUrl(getStringValueFromPropertyString(p.getValue()));
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_API_TOKEN.value())) {
        config.setRewardApiToken(p.getValue());
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.JURISDICTION.value())) {
        config.setJurisdiction(getStringValueFromPropertyString(p.getValue()));
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.COUNTRY_CODE.value())) {
        config.setCountryCode(p.getValue());
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BRAND_ID.value())) {
        config.setBrandId(p.getValue());
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.IFORIUM_PLATFORM_KEY.value())) {
        config.setIforiumPlatformKey(p.getValue());
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PLAYER_OFFSET.value())) {
        config.setPlayerOffset(p.getValue());
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PLAYER_GUID_PREFIX.value())) {
        config.setPlayerGuidPrefix(p.getValue());
      }

    }
    return config;
  }

  private ProviderClient getProviderService() {
    ProviderClient cl = null;
    try {
      cl = services.target(ProviderClient.class, "service-domain", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting provider properties", e);
    }
    return cl;
  }

  private String getStringValueFromPropertyString(String stringValue) {
    if (stringValue != null && !stringValue.trim().isEmpty()) {
      return stringValue;
    }

    return null;
  }

  private Boolean getBooleanValueFromPropertyString(String stringValue) {
    if (stringValue != null && !stringValue.trim().isEmpty()) {
      return Boolean.parseBoolean(stringValue);
    }

    return Boolean.FALSE;
  }

  private Integer getIntegerValueFromPropertyString(String stringValue) {
    if (stringValue != null && !stringValue.trim().isEmpty()) {
      try {
        return Integer.parseInt(stringValue);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}