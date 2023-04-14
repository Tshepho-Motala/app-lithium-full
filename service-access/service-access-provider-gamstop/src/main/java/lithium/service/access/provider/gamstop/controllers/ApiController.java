package lithium.service.access.provider.gamstop.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.gamstop.data.enums.ExclusionType;
import lithium.service.access.provider.gamstop.data.objects.SelfExclusionResponse;
import lithium.service.access.provider.gamstop.services.ApiService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.User;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static lithium.service.Response.Status.INVALID_DATA;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
public class ApiController extends BaseController {

	@Autowired
	private ApiService apiService;
	@Autowired
	private LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private CachingDomainClientService cachingDomainClientService;
	@Autowired
	MessageSource messageSource;

	public Response<ProviderAuthorizationResult> checkExclusion(
		String url,
		String apiKey,
		String playerguid,
		PlayerBasic playerBasic,
		Integer connectTimeout,
		Integer connectionRequestTimeout,
		Integer socketTimeout
	) throws Status551ServiceAccessClientException, UserClientServiceFactoryException {
		log.debug("playerguid {}", playerguid);
		ArrayList rawListData = new ArrayList<RawAuthorizationData>();
		User user = null;
		RawAuthorizationData rawAuthorizationData = new RawAuthorizationData();
		SelfExclusionResponse exclusionResponse = null;
		rawListData.add(rawAuthorizationData);
		ProviderAuthorizationResult providerAuthorizationResult = ProviderAuthorizationResult.builder().build();
		providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);

		try {
			user = apiService.getUser(playerguid);
		} catch (Exception e) {
			String message = "Unable to get the user from service-user, this could be normal if it's a pre-registration rule: "+e.getMessage();
			if (!log.isDebugEnabled()) {
				log.info(message);
			}
			log.debug(message + " | playerGuid: " + playerguid + ", playerBasic: " + playerBasic, e);
		}

		if (user == null) {
			// This means it is a pre-user creation scenario, we should only perform external check and respond
			exclusionResponse = apiService.checkExclusionRaw(
					url,
					apiKey,
					playerBasic,
					rawAuthorizationData,
					connectTimeout,
					connectionRequestTimeout,
					socketTimeout);
		}

		if (exclusionResponse == null) { // Means there was a user object, thus no raw check took place using playerbasic
			if (apiService.alreadyBlocked(playerguid)) {
				providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
				return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(OK).build();
			}
			exclusionResponse = apiService.checkExclusion(
					user,
					url,
					apiKey,
					rawAuthorizationData,
					connectTimeout,
					connectionRequestTimeout,
					socketTimeout);
		}
		Response.Status status;
		if (exclusionResponse != null && exclusionResponse.getExclusionType() == ExclusionType.N) {
			status = OK;
			providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
		} else if (exclusionResponse != null && exclusionResponse.getExclusionType() == ExclusionType.P) {
			String defaultLocale = cachingDomainClientService.domainLocale(user.getDomain().getName());
			String comment = "";
			status = OK;
			providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);

			if (user.getHasSelfExcluded() == null) {
				try {
					getUserApiInternalClient().markHasSelfExcludedAndOptOutComms(user.guid());
				} catch (Exception e) {
					log.warn("Failed to mark player: "+ user.getUsername() +" with self excluded flag in " + "svc-user | " + e.getMessage(), e);
				}

				try {
					if (user.getLastLogin() == null) {
						comment += " " + messageSource.getMessage("UI_NETWORK_ADMIN.ACCESS_PROVIDER_GAMSTOP.SE_GAMSTOP_HISTORY", null, Locale.forLanguageTag(defaultLocale));
						List<ChangeLogFieldChange> clfc = changeLogService.copy(user, new User(),
								new String[]{"guid", "createdDate", "status"});
						changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "create", user.getId(), User.SYSTEM_GUID,
								null, comment,
								null, clfc, Category.ACCOUNT, SubCategory.STATUS_CHANGE, 70, user.getDomain().getName());
					}
				} catch (Exception e) {
					String msg = "Changelog registration for player exclusion create failed";
					log.error(msg + " [playerGuid=" + user.guid() + ", authorGuid=" + User.SYSTEM_GUID + "]" + e.getMessage(), e);
				}
			}
		} else if (exclusionResponse != null && exclusionResponse.getExclusionType() == ExclusionType.Y) {
			status = OK;
			providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
		} else {
			status = INVALID_DATA;
			providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
		}
		providerAuthorizationResult.setRawDataList(rawListData);
		log.debug("AuthorizationResult : "+providerAuthorizationResult);
		return Response.<ProviderAuthorizationResult>builder().status(status).data(providerAuthorizationResult).build();
	}

	private UserApiInternalClient getUserApiInternalClient() throws LithiumServiceClientFactoryException {
		UserApiInternalClient client = lithiumServiceClientFactory.target(UserApiInternalClient.class,
				"service-user", true);
		return client;
	}

}
