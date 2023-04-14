package lithium.service.user.provider.threshold.extremepush;

import java.util.Arrays;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.modules.ModuleInfo;
import lithium.service.user.provider.threshold.config.ProviderConfig;
import lithium.service.user.provider.threshold.config.ProviderConfigService;
import lithium.service.user.provider.threshold.extremepush.dto.ThresholdMessage;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExtremePushServiceImpl implements ExtremePushService {

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ProviderConfigService providerConfigService;
  @Autowired
  private ModuleInfo moduleInfo;

  @Override
  public ResponseEntity<?> sendMessage(ThresholdMessage message) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<?> entity = new HttpEntity<>(message, headers);
    ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(),
        message.getDomainName());
    if (config.getExtremePushApiUrl() != null) {
      UrlValidator urlValidator = new UrlValidator();
      boolean urlIsValid = urlValidator.isValid(config.getExtremePushApiUrl());
      if (!urlIsValid) {
        throw new Status512ProviderNotConfiguredException("Extreme Push URL is not configured");
      }
      ResponseEntity body = restTemplate.exchange(config.getExtremePushApiUrl(), HttpMethod.POST,
          entity, Object.class);
      return body;
    } else {
      throw new Status512ProviderNotConfiguredException("Extreme Push URL is not configured");
    }
  }
}
