package lithium.service.mail.provider.kickbox.services;

import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import lithium.service.mail.provider.kickbox.objects.KickboxResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KickboxEmailVerificationServiceTest {
  private EmailVerificationService emailVerificationService = new EmailVerificationService();
  private RestTemplate restTemplate;
  private Logger logger;

  @Before
  public void setup() {
    restTemplate = Mockito.mock(RestTemplate.class);
    logger = Mockito.mock(Logger.class);
    emailVerificationService.setLogger(logger);
    emailVerificationService.setRestTemplate(restTemplate);
  }


  @Test
  public void shouldPassWhenResultIsDeliverable() throws Status403InvalidProviderCredentials {

    KickboxResponse response = KickboxResponse.builder()
            .result("deliverable")
            .reason("accepted_email")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);

    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder()
                    .properties(new HashMap<>())
                    .build());

    assertTrue(result.isSuccess());
    Mockito.verify(logger, Mockito.times(1)).debug(Mockito.anyString(), ArgumentMatchers.<ResponseEntity<KickboxResponse>>any());

  }

  @Test
  public void shouldFailWhenResultIsNotDeliverable() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("invalid_email").build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertFalse(result.isSuccess());
    Mockito.verify(logger, Mockito.times(1)).debug(Mockito.anyString(), ArgumentMatchers.<ResponseEntity<KickboxResponse>>any());

  }


  @Test(expected = HttpServerErrorException.class)
  public void shouldFailedAndLogExceptionWhenOnRequestIsNotSuccessful() throws Status403InvalidProviderCredentials {
    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(!result.isSuccess());
    Mockito.verify(logger, Mockito.times(1)).error(Mockito.anyString(), Mockito.any(Exception.class));

  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsEmailAccepted() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("accepted_email")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsLowQuality() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("low_quality")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsLowDeliverability() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("low_deliverability")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsNoConnect() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("no_connect")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsTimeout() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("timeout")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsInvalidSmtp() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("invalid_smtp")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsUnvailableSmtp() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("unavailable_smtp")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldPassWhenIsNotDeliverableAndReasonIsUnexpectedError() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("unexpected_error")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(result.isSuccess());
  }

  @Test
  public void shouldFailWhenIsNotDeliverableAndReasonIsUnsupported() throws Status403InvalidProviderCredentials {
    KickboxResponse response = KickboxResponse.builder()
            .result("undeliverable")
            .reason("invalid_email")
            .build();
    ResponseEntity<KickboxResponse> responseEntity = ResponseEntity.status(200).body(response);

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(responseEntity);
    EmailVerificationResult result = emailVerificationService.verify(VerifyEmailRequest.builder().properties(new HashMap<>()).build());

    assertTrue(!result.isSuccess());
  }
}
