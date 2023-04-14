package lithium.service.mail.provider.kickbox.api.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.mail.client.EmailVerifyClient;
import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import lithium.service.mail.provider.kickbox.services.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



/**
 * EmailVerificationController
 */
@RestController
public class EmailVerificationController implements EmailVerifyClient {

  @Autowired
  private EmailVerificationService emailVerificationService;

  @RequestMapping(value="/frontend/verify-email", method = RequestMethod.POST)
  public Response<EmailVerificationResult> verify(@RequestBody VerifyEmailRequest request) throws Status403InvalidProviderCredentials {
    return Response.<EmailVerificationResult>builder().status(Status.OK_SUCCESS).data(
        emailVerificationService.verify(request)
    ).build();
  }
}
