package lithium.service.access.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;

@FeignClient("service-access")
public interface AuthorizationClient {

	@RequestMapping("/authorization/{domainName}/{accessRuleName}/checkAuthorization")
	public Response<AuthorizationResult> checkAuthorization(
		@PathVariable("domainName") String domainName,
		@PathVariable("accessRuleName") String accessRuleName,
		@RequestBody AuthorizationRequest authorizationRequest
	);

	@RequestMapping("/authorization/{domainName}/{accessRuleName}/isAccessRuleEnabled")
	public Response<Boolean> isAccessRuleEnabled(
		@PathVariable("domainName") String domainName,
		@PathVariable("accessRuleName") String accessRuleName,
		@RequestBody AuthorizationRequest authorizationRequest
	);

}
