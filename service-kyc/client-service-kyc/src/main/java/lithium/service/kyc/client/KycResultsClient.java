package lithium.service.kyc.client;

import feign.Headers;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.kyc.client.exceptions.Status459VerificationResultNotFountException;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.exceptions.Status400BadRequestException;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status427UserKycVerificationLifetimeAttemptsExceeded;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status504KycProviderEndpointUnavailableException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.schema.VerificationStatusResponse;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name="service-kyc")
public interface KycResultsClient {

    @RequestMapping(method= RequestMethod.POST, path="/system/result/save")
    @Headers("Content-Type: application/json")
    Response<VerificationResult> addVerificationResult(@RequestBody VerificationKycAttempt attempt);

	@RequestMapping(method= RequestMethod.POST, path="/system/result/update")
	@Headers("Content-Type: application/json")
	Response<VerificationResult> updVerificationResult(@RequestBody VerificationKycAttempt attempt) throws Status459VerificationResultNotFountException;

	@RequestMapping(method= RequestMethod.POST, path="/system/verify")
	public ResponseEntity<VerificationStatusResponse> verify(@RequestBody VerifyRequest verifyRequest)
			throws LithiumServiceClientFactoryException, UserNotFoundException, UserClientServiceFactoryException,
			Status520KycProviderEndpointException, Status504KycProviderEndpointUnavailableException, Status515SignatureCalculationException,
			Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status424KycVerificationUnsuccessfulException,
			Status512ProviderNotConfiguredException, Status425IllegalUserStateException, Status400BadRequestException, Status550ServiceDomainClientException,
			Status513StatsServiceUnavailableException, Status427UserKycVerificationLifetimeAttemptsExceeded, Status500InternalServerErrorException,
			Status428KycMismatchLastNameException, Status429KycMismatchDobException;
}
