package lithium.service.access.client;

import lithium.service.Response;
import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-access")
public interface ExternalAuthorizationClient {
//	@RequestMapping("/external/checkAuthorization")
//	public Response<AuthorizationResult> checkAuthorization(
//			@RequestBody ExternalAuthorizationRequest externalAuthorizationRequest
//	);
	@RequestMapping("/system/checkAuthorization")
	public Response<ProviderAuthorizationResult> checkAuthorization(
			@RequestBody ExternalAuthorizationRequest externalAuthorizationRequest
	) throws Status513InvalidDomainConfigurationException, Status551ServiceAccessClientException, Status550ServiceDomainClientException;
}
