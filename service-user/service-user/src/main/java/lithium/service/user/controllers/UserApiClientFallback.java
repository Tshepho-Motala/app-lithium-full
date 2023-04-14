package lithium.service.user.controllers;

import java.util.List;

import lithium.service.Response;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserApiToken;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
class UserApiClientFallback implements UserApiClient {
	@Override
	public Response<UserApiToken> saveApiToken(String guid, String apiToken) {
		log.warn("fallback called for saveApiToken.");
		return Response.<UserApiToken>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}
	@Override
	public Response<UserApiToken> getApiTokenIfValid(String guid, String apiToken) {
		log.warn("fallback called for isApiTokenValid.");
		return Response.<UserApiToken>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}
	@Override
	public Response<User> getUser(String guid, String apiToken) {
		log.warn("fallback called for getUser through api.");
		return Response.<User>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}
	@Override
	public List<User> usersByDomainAndLabel(String domainName, String labelName) {
		log.warn("fallback called for usersByDomainAndLabel through api.");
		return null;
	}
	@Override
	public Response<User> getUserByApiToken(String apiToken) {
		log.warn("fallback called for usersByDomainAndLabel through api.");
		return null;
	}
	@Override
	public Response<UserApiToken> getApiTokenByUserGuid(String userGuid) {
		log.warn("fallback called for getUserByUserGuid through api.");
		return null;
	}

	@Override
	public Response<String> getUserGuidByShortGuid(String shortGuid) {
		log.warn("fallback called for getUserGuidByShortGuid through api.");
		return null;
	}
}
