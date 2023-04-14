package lithium.service.user.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;


@FeignClient(name="service-user")
public interface UserApiClient {
	
	@RequestMapping("/userapi/getApiTokenIfValid")
	public Response<lithium.service.user.client.objects.UserApiToken> getApiTokenIfValid(
		@RequestParam("guid") String guid,
		@RequestParam("apiToken") String apiToken
	);
	
	@RequestMapping("/userapi/saveApiToken")
	public Response<lithium.service.user.client.objects.UserApiToken> saveApiToken(
		@RequestParam("guid") String guid,
		@RequestParam("apiToken") String apiToken
	);
	
	@RequestMapping("/userapi/getUser")
	public Response<lithium.service.user.client.objects.User> getUser(
		@RequestParam("guid") String guid,
		@RequestParam(name="apiToken", required=false) String apiToken
	);

	@RequestMapping("/{domainName}/users/usersByDomainAndLabel")
	public List<lithium.service.user.client.objects.User> usersByDomainAndLabel(
		@PathVariable("domainName") String domainName,
		@RequestParam("labelName") String labelName
	);
	
	@RequestMapping("/userapi/getUserByApiToken")
	public Response<lithium.service.user.client.objects.User> getUserByApiToken(
		@RequestParam(name="apiToken") String apiToken);

	@RequestMapping("/userapi/getUserGuidByShortGuid")
	public Response<String> getUserGuidByShortGuid(
		@RequestParam(name="shortGuid") String shortGuid);

	@RequestMapping("/userapi/getApiTokenByUserGuid")
	public Response<lithium.service.user.client.objects.UserApiToken> getApiTokenByUserGuid(
		@RequestParam(name="guid") String guid);
}
