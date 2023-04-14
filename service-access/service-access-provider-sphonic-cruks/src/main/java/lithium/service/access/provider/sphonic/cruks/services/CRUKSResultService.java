package lithium.service.access.provider.sphonic.cruks.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.client.ExclusionClient;
import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.client.schemas.exclusion.ExclusionRequest;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CRUKSResultService {
	@Autowired private ChangeLogService changeLogService;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private UserApiInternalClientService userApiInternalClientService;

	public static final String CRUKS_RESULT_PASS = "PASS";
	public static final String CRUKS_RESULT_FAIL = "FAIL";
	public static final String CRUKS_RESULT_NONE = "NONE";
	public static final String CRUKS_RESULT_ERROR = "ERROR";
	public static final String CRUKS_RESULT_INVALID = "INVALID";
	public static final String CRUKS_RESULT_REVIEW = "REVIEW";

	private static final String EXCLUSION_ADVISOR_CRUKS = "CRUKS";

	public static final String CRUKS_RESULT_NONE_MESSAGE = "CRUKS API failed to get a valid response from Sphonic: result = NONE";
	public static final String CRUKS_RESULT_ERROR_MESSAGE = "CRUKS API failed to get a valid response from Sphonic: result = ERROR";

	public void handle(User user, String cruksId, String result, boolean logoutUserOnFail) {
		switch (result) {
			// TODO: Player is not excluded on CRUKS.
			//       Is it possible for a player's exclusion on CRUKS to be removed? If it is, then we need to
			//       check our internal self exclusion and remove CRUKS exclusion + restrictions, if exists.
			//       However, the login flow doesn't allow past the SE + restriction once it is in place.
			case CRUKS_RESULT_FAIL:
				cruksSelfExlcusion(user, cruksId, logoutUserOnFail);
				break;
			case CRUKS_RESULT_NONE:
			case CRUKS_RESULT_INVALID:
				break;
			default:;
		}
	}

	private void cruksSelfExlcusion(User user, String cruksId, boolean logoutUserOnFail) {
		// Player is excluded on CRUKS.
		String playerStatus = (user.getStatus() != null) ? user.getStatus().getName().toUpperCase() : "UNKNOWN";
		String comments = "The user.exclusion was checked. Cruks ID " + cruksId + " is invalid and account"
				+ " status was changed from ["+playerStatus+"] to [Account Frozen - CRUKS Self Excluded.]";
		addNote(user.getId(), user.getDomain().getName(), comments, 80);
		addPlayerExclusion(user.guid(), cruksId);
		if (logoutUserOnFail) {
			try {
				userApiInternalClientService.logout(user.guid());
			} catch (Exception e) {
				log.error("Failed to logout user [user.guid="+ user.guid()+"] " + e.getMessage(), e);
			}
		}
	}

	private void addNote(Long userId, String domainName, String comments, int priority) {
		changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "edit", userId,
				User.SYSTEM_GUID, null, comments, null, null,
				Category.ACCOUNT, SubCategory.STATUS_CHANGE, priority, domainName);
	}

	private void addPlayerExclusion(String playerGuid, String cruksId) {
		try {
			Optional<ExclusionClient> client = getClient(ExclusionClient.class, "service-limit");
			client.get().set(ExclusionRequest.builder()
					.playerGuid(playerGuid)
					.advisor(EXCLUSION_ADVISOR_CRUKS)
					.exclusionSource(ExclusionSource.EXTERNAL)
					.build());
		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("CRUKSResultService.addPlayerExclusion failed [playerGuid="+playerGuid
					+ ", cruksId="+cruksId+" | " + e.getMessage(), e);
		}
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}
}
