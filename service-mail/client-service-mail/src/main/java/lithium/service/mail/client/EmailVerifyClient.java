package lithium.service.mail.client;

import lithium.service.Response;

import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient
public interface EmailVerifyClient {
    @RequestMapping(value="/frontend/verify-email", method = RequestMethod.POST)
    Response<EmailVerificationResult> verify(@RequestBody VerifyEmailRequest request) throws Status403InvalidProviderCredentials;
}
