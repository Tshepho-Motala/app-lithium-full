package lithium.service.mail.provider.kickbox.services;

import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import lithium.service.mail.provider.kickbox.config.ProviderConfigProperties;
import lithium.service.mail.provider.kickbox.objects.KickboxResponse;
import lithium.service.mail.provider.kickbox.objects.VerificationReason;
import lithium.service.mail.provider.kickbox.objects.VerificationStatus;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Setter
public class EmailVerificationService {
  @Autowired
  private RestTemplate restTemplate;

  private Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);


  public EmailVerificationResult verify(VerifyEmailRequest request) throws Status403InvalidProviderCredentials {

    Map<String, String> properties = request.getProperties();
    String key = properties.get(ProviderConfigProperties.API_KEY.getValue());
    String url = properties.get(ProviderConfigProperties.URL.getValue());
    EmailVerificationResult result = null;

    String endpoint = MessageFormat
        .format("{0}?apikey={1}&email={2}",url, key,request.getEmail());

    try {
      ResponseEntity<KickboxResponse> responseEntity = restTemplate.getForEntity(endpoint, KickboxResponse.class);

      if(responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new Status403InvalidProviderCredentials("Invalid Api key");
      }

      KickboxResponse kickboxResponse = responseEntity.getBody();
      VerificationStatus verificationStatus = VerificationStatus.getFrom(kickboxResponse.getResult());
      VerificationReason reason = VerificationReason.getFrom(kickboxResponse.getReason());

      result = EmailVerificationResult.builder()
              .message(reason.getMessage())
              .success(verificationStatus == VerificationStatus.DELIVERABLE || isAllowedReason(reason))
              .build();
      logger.debug("Kickbox Response ", responseEntity);
    }
    catch (HttpServerErrorException e) {
      logger.error(e.getMessage(), e);
      throw e;
    }

    return result;
  }

  public boolean isAllowedReason(VerificationReason reason) {
    List<VerificationReason> reasonsToReject = Arrays.asList(VerificationReason.INVALID_EMAIL, VerificationReason.INVALID_DOMAIN, VerificationReason.REJECTED_EMAIL);

    return Stream.of(VerificationReason.values()).filter(r -> !reasonsToReject.contains(r))
            .collect(Collectors.toList())
            .contains(reason);
  }
}
