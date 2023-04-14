package lithium.service.user.threshold.service.impl;

import java.util.Arrays;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.modules.ModuleInfo;
import lithium.service.mail.client.exceptions.Status500ProviderNotConfiguredException;
import lithium.service.user.threshold.config.DomainProviderConfig;
import lithium.service.user.threshold.config.DomainProviderConfigService;
import lithium.service.user.threshold.data.dto.ExternalServiceMessage;
import lithium.service.user.threshold.service.ExtremePushService;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExtremePushServiceImpl implements ExtremePushService {

  private RestTemplate restTemplate;

  @Autowired
  public ExtremePushServiceImpl(@Qualifier( "lithium.rest" ) RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  @Autowired
  private DomainProviderConfigService domainProviderConfigService;
  @Autowired
  private ModuleInfo moduleInfo;

  @Override
  public ResponseEntity<?> sendMessage(ExternalServiceMessage message) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<?> entity = new HttpEntity<>(message, headers);
    DomainProviderConfig config;
    try {
      config = domainProviderConfigService.getConfig(moduleInfo.getModuleName(), message.getDomainName());
    } catch (Status500ProviderNotConfiguredException e) {
      throw new Status512ProviderNotConfiguredException(e.getMessage());
    }

    message.setApptoken(config.getExtremePushAppToken());
    if ((config.getExtremePushApiUrl() != null) && (config.getExtremePushAppToken() != null)) {
      UrlValidator urlValidator = new UrlValidator();
      boolean urlIsValid = urlValidator.isValid(config.getExtremePushApiUrl());
      if (!urlIsValid) {
        throw new Status512ProviderNotConfiguredException("Extreme Push URL is not configured");
      }
      return restTemplate.exchange(config.getExtremePushApiUrl(), HttpMethod.POST, entity, Object.class);
    } else {
      throw new Status512ProviderNotConfiguredException("Extreme Push URL is not configured");
    }
  }
}
