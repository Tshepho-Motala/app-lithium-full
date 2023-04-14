package lithium.service.kyc.provider.clients;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status504KycProviderEndpointUnavailableException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.KycBank;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VerifyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient
public interface KycVerifyClient {
    @RequestMapping(path = "/system/verify", method = RequestMethod.POST)
    public ResponseEntity<KycSuccessVerificationResponse> verify(@RequestBody VerifyRequest verifyRequest)

            throws Status512ProviderNotConfiguredException, Status520KycProviderEndpointException, Status515SignatureCalculationException,
            Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status504KycProviderEndpointUnavailableException,
            Status424KycVerificationUnsuccessfulException, Status500InternalServerErrorException, Status428KycMismatchLastNameException,
            Status429KycMismatchDobException;

	@RequestMapping(path = "/system/banks", method = RequestMethod.GET)
	public List<KycBank> banks(@RequestParam("domainName") String domainName) throws Exception;
}
