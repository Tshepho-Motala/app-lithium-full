package lithium.service.user.provider.sphonic.idin.controllers;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status463IncompleteUserRegistrationException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.ExternalUserDetailsRequest;
import lithium.service.user.client.objects.ExternalUserDetailsResponse;
import lithium.service.user.client.objects.PostRegistrationSteps;
import lithium.service.user.client.objects.ValidatePreRegistration;
import lithium.service.user.client.objects.ValidatePreRegistrationResponse;
import lithium.service.user.provider.sphonic.idin.objects.IncompleteUserStatus;
import lithium.service.user.provider.sphonic.idin.services.ExternalProviderIDINToKycService;
import lithium.service.user.provider.sphonic.idin.services.ExternalProviderIdinService;
import lithium.service.user.provider.sphonic.idin.services.IDINRegistrationStepsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/system")
/**
 * {@code ExternalUserProviderIdnController}
 * This controller provides iDin integration functionality for address verification
 */
public class ExternalUserProviderIdnController {

    private ExternalProviderIdinService externalProviderService;
    private IDINRegistrationStepsService idinRegistrationStepsService;
    private MessageSource messageSource;
    private ExternalProviderIDINToKycService idinToKycService;

   @Autowired
    public ExternalUserProviderIdnController(ExternalProviderIdinService externalProviderService, IDINRegistrationStepsService idinRegistrationStepsService, MessageSource messageSource,
                                             ExternalProviderIDINToKycService idinToKycService) {
        this.externalProviderService = externalProviderService;
        this.idinRegistrationStepsService = idinRegistrationStepsService;
        this.messageSource = messageSource;
        this.idinToKycService = idinToKycService;
    }

    @PostMapping("/external-register")
    public Response<ExternalUserDetailsResponse> externalRegister(@RequestBody ExternalUserDetailsRequest userDetailsRequest) throws Status400BadRequestException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status550ServiceDomainClientException {
        ExternalUserDetailsResponse externalUserDetailsResponse = externalProviderService.sendRequestToIdin(userDetailsRequest);
        if(externalUserDetailsResponse.getStatus() == null || externalUserDetailsResponse.getStatus().equals(IncompleteUserStatus.TIMEOUT.id())) {
            return Response.<ExternalUserDetailsResponse>builder().status(Response.Status.SERVER_TIMEOUT).build();
        } else {
            return Response.<ExternalUserDetailsResponse>builder().data(externalUserDetailsResponse).status(
                    Response.Status.OK_SUCCESS).build();
        }
    }

    @PostMapping("/do-post-registration-steps")
    public Response<ValidatePreRegistrationResponse> postRegistrationSteps(@RequestBody PostRegistrationSteps postRegistrationSteps) {
        Response<ValidatePreRegistrationResponse> res =idinRegistrationStepsService.doPostRegistrationSteps(postRegistrationSteps);
       return res;
    }

    @PostMapping("/validate-pre-registration")
    public Response<ValidatePreRegistrationResponse> validatePreRegistration(@RequestBody ValidatePreRegistration validatePreRegistration) {
        return idinRegistrationStepsService.preRegistrationValidation(validatePreRegistration);
    }
}
