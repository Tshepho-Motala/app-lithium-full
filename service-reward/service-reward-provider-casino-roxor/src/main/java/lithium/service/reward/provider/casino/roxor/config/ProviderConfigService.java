package lithium.service.reward.provider.casino.roxor.config;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProviderConfigService {

  @Autowired
  LithiumServiceClientFactory services;

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
      throw new Exception(domainName);
    }

    Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

    if (!pp.isSuccessful() || pp.getData() == null) {
      throw new Exception(domainName);
    }

    ProviderConfig config = new ProviderConfig();
    for (ProviderProperty p: pp.getData()) {
      if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_URL.value())) {
        config.setRewardsUrl(getStringValueFromPropertyString(p.getValue()));
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_DEFAULT_DURATION_IN_HOURS.value())) {
        config.setRewardsDefaultDurationInHours(getIntegerValueFromPropertyString(p.getValue()));
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.WEBSITE.value())) {
        config.setWebsite(getStringValueFromPropertyString(p.getValue()));
      } else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.USE_PLAYER_API_TOKEN.value())) {
        config.setUsePlayerApiToken(getBooleanValueFromPropertyString(p.getValue()));
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