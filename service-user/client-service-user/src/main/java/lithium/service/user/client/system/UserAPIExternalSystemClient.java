package lithium.service.user.client.system;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.ExternalUserDetailsRequest;
import lithium.service.user.client.objects.ExternalUserDetailsResponse;
import lithium.service.user.client.objects.PostRegistrationSteps;
import lithium.service.user.client.objects.ValidatePreRegistration;
import lithium.service.user.client.objects.ValidatePreRegistrationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "service-user-provider-sphonic-idin")
public interface UserAPIExternalSystemClient {

    @PostMapping("/system/external-register")
    Response<ExternalUserDetailsResponse> externalRegister(@RequestBody ExternalUserDetailsRequest userDetailsRequest) throws Status400BadRequestException,
            Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status550ServiceDomainClientException;

    @PostMapping("/system/do-post-registration-steps")
    Response<ValidatePreRegistrationResponse> postRegistrationSteps(@RequestBody PostRegistrationSteps postRegistrationSteps);

    @PostMapping("/system/validate-pre-registration")
    Response<ValidatePreRegistrationResponse> validatePreRegistration(@RequestBody ValidatePreRegistration validatePreRegistration);

}
