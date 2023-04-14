package lithium.service.mail.api.controllers;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.exceptions.Status500ProviderNotConfiguredException;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import lithium.service.mail.services.DomainProviderService;
import lithium.service.mail.services.EmailVerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableCustomHttpErrorCodeExceptions
public class FrontendEmailVerificationController {

    @Autowired
    DomainProviderService domainProviderService;

    @Autowired
    LithiumServiceClientFactory serviceClientFactory;

    @Autowired
    EmailVerificationService emailVerificationService;

    @RequestMapping(value = "/frontend/verify-email", method = RequestMethod.POST)
    public Response<EmailVerificationResult> verify(@RequestBody VerifyEmailRequest request)
            throws LithiumServiceClientFactoryException,
            Status500ProviderNotConfiguredException, Status401UnAuthorisedException, Status403InvalidProviderCredentials {

        return emailVerificationService.verify(request);
    }

}
