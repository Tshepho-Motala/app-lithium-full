package lithium.service.access.provider.google.recaptcha.controller;

import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.Response;
import lithium.service.access.provider.google.recaptcha.services.GoogleRecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class ExternalAuthorizationWrapperController implements ExternalAuthorizationClient {

    @Autowired
    GoogleRecaptchaService service;

    @Override
    @RequestMapping(path = "/checkAuthorization")
    public Response<ProviderAuthorizationResult> checkAuthorization(
            @RequestBody ExternalAuthorizationRequest externalAuthorizationRequest) {
        return Response.<ProviderAuthorizationResult>builder()
                .data(service.checkAuthorization(externalAuthorizationRequest))
                .status(Response.Status.OK)
                .build();
    }
}
