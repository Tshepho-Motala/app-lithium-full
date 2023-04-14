package lithium.service.access.provider.transunion.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.provider.transunion.exeptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.transunion.service.TransUnionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lithium.service.Response.Status.ACCOUNT_UPGRADE_REQUIRED;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;
import static lithium.service.Response.Status.UNAUTHORIZED;
import static lithium.service.access.client.objects.EAuthorizationOutcome.NOT_FILLED;

@Slf4j
@RestController
@EnableUserApiInternalClientService
@RequestMapping("/system")
public class ApiController implements ExternalAuthorizationClient {
    @Autowired
    TransUnionService transUnionService;
    @Autowired
    private UserApiInternalClientService userService;

    @Override
    @RequestMapping(path = "/checkAuthorization")
    public Response<ProviderAuthorizationResult> checkAuthorization(
            @RequestBody ExternalAuthorizationRequest externalAuthorizationRequest) {
        User user = null;
        ProviderAuthorizationResult providerAuthorizationResult = new ProviderAuthorizationResult();

        try {
            user = userService.getUserByGuid(externalAuthorizationRequest.getUserGuid());
        } catch (UserClientServiceFactoryException | UserNotFoundException e) {
            log.error("Can not find User for TransUnion validation for request " + externalAuthorizationRequest.toString() + " " + e.getLocalizedMessage());
            providerAuthorizationResult.setErrorMessage(e.getMessage());
            return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(NOT_FOUND).build();
        }

        try {
            providerAuthorizationResult = transUnionService.doVerify(user, providerAuthorizationResult);
        } catch (Status512ProviderNotConfiguredException e) {
            log.error("Can't start verify process, service is not configured properly " + e.getMessage());
            providerAuthorizationResult.setErrorMessage(e.getMessage());
            return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(UNAUTHORIZED).build();
        } catch (Status500InternalServerErrorException e) {
            log.error("Can't start verify process, internal server error:" + e.getMessage());
            return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(INTERNAL_SERVER_ERROR).build();
        }

        log.debug("AuthorizationResult : " + providerAuthorizationResult);

        if (providerAuthorizationResult.getAuthorisationOutcome().equals(NOT_FILLED)) {
            return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(ACCOUNT_UPGRADE_REQUIRED).build();
        }
        return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(OK).build();
    }
}
