package lithium.service.access.provider.gamstop.controllers;

import lithium.service.Response;
import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.gamstop.exceptions.Status424InvalidRequestException;
import lithium.service.access.client.gamstop.exceptions.Status512ExclusionCheckException;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckResponse;
import lithium.service.access.client.gamstop.objects.CheckExclusionRequest;
import lithium.service.access.client.gamstop.objects.CheckExclusionResponse;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.provider.gamstop.config.APIAuthentication;
import lithium.service.access.provider.gamstop.services.ApiService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.report.client.players.exceptions.Status551ServiceReportClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static lithium.service.Response.Status.OK_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/system")
public class AuthorizationWrapperController implements ExternalAuthorizationClient {
	@Autowired ApiController apiController;
	@Autowired LithiumServiceClientFactory services;
	@Autowired private ApiService apiService;

	@Override
	@RequestMapping(path = "/checkAuthorization")
	public Response<ProviderAuthorizationResult> checkAuthorization(@RequestBody ExternalAuthorizationRequest request) throws Status513InvalidDomainConfigurationException, Status551ServiceAccessClientException, Status550ServiceDomainClientException {
		log.debug("ExternalAuthorizationRequest {}", request);
		APIAuthentication apiAuth = apiController.getAPIAuthentication("service-access-provider-gamstop", request.getDomainName());
		try {
			return  apiController.checkExclusion(apiAuth.getBrandConfiguration().getPlatformUrl(),
					apiAuth.getBrandConfiguration().getApiKey(),
					request.getUserGuid(),
					request.getPlayerBasic(),
					apiAuth.getBrandConfiguration().getConnectTimeout(),
					apiAuth.getBrandConfiguration().getConnectionRequestTimeout(),
					apiAuth.getBrandConfiguration().getSocketTimeout());
		} catch (UserClientServiceFactoryException e) {
			log.error("Problem getting user client service", e);
			throw new Status513InvalidDomainConfigurationException(e.getMessage());
		}
	}

	@RequestMapping(path = "/batch/checkAuthorization")
	public Response<BatchExclusionCheckResponse> batchExclusionCheck(
			@RequestBody BatchExclusionCheckRequest batchExclusionCheckRequest
	) throws Status512ExclusionCheckException, Status424InvalidRequestException, Status513InvalidDomainConfigurationException, Status551ServiceAccessClientException, Status551ServiceReportClientException, UserClientServiceFactoryException, Status550ServiceDomainClientException {
		BatchExclusionCheckResponse checkResponse = apiService.batchExclusionCheck(batchExclusionCheckRequest);
		return Response.<BatchExclusionCheckResponse>builder().status(OK_SUCCESS).data(checkResponse).build();
	}

	@RequestMapping("/check-exclusion")
	public Response<CheckExclusionResponse> checkExclusion(@RequestBody CheckExclusionRequest checkExclusionRequest) {
		ExternalAuthorizationRequest request = ExternalAuthorizationRequest.builder()
				.domainName(checkExclusionRequest.getDomainName())
				.userGuid(checkExclusionRequest.getUserGuid())
				.build();
		try {
			log.debug("{}",request);
			checkAuthorization(request);
			return Response.<CheckExclusionResponse>builder().status(OK_SUCCESS).build();
		} catch (Status513InvalidDomainConfigurationException | Status551ServiceAccessClientException | Status550ServiceDomainClientException e) {
			log.error("Failed to check exclusion, {}", checkExclusionRequest, e);
		}
		return null;
	}

}
