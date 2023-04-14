package lithium.service.user.client.service;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.UserPubSubClient;
import lithium.service.user.client.UserStatusClient;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.objects.StatusReason;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdateBasic;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.objects.EcosystemUserProfiles;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class UserApiInternalClientService {

    @Autowired
    LithiumServiceClientFactory factory;
    @Autowired
    MessageSource messageSource;
	@Autowired
	CachingDomainClientService cachingDomainClientService;

	public EcosystemUserProfiles getEcosystemUserProfileResponse(User user) throws LithiumServiceClientFactoryException {
		if(user != null && cachingDomainClientService.isDomainInAnyEcosystem(user.guid().split("/")[0])) {
			UserApiInternalClient client = getClient();
			Response<EcosystemUserProfiles> response = client.getEcosystemUserProfile(user.getId());
			if (!response.isSuccessful()) throw new LithiumServiceClientFactoryException(response.getMessage());
			return response.getData();
		}
		return EcosystemUserProfiles.builder().build();
	}

	public User getUserByEmail(String domainName, String email) throws UserClientServiceFactoryException, UserNotFoundException {
		try {
			Response<User> response = getClient().getUserByEmail(domainName, email);
			if (response.getStatus() == Response.Status.NOT_FOUND) throw new UserNotFoundException();
			if (!response.isSuccessful()) throw new UserClientServiceFactoryException(response.getMessage());
			return response.getData();
		} catch (LithiumServiceClientFactoryException e) {
			throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
		}
	}

    public User getUserByCellphoneNumber(String domainName, String cellphoneNumber) throws UserClientServiceFactoryException, UserNotFoundException {
        try {
            Response<User> response = getClient().getUserByCellphoneNumber(domainName, cellphoneNumber);
            if (response.getStatus() == Response.Status.NOT_FOUND) throw new UserNotFoundException();
            if (!response.isSuccessful()) throw new UserClientServiceFactoryException(response.getMessage());
            return response.getData();
        } catch (LithiumServiceClientFactoryException e) {
            throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

    public User getUserByGuid(String guid) throws UserClientServiceFactoryException, UserNotFoundException {
        try {
            Response<User> response = getClient().getUser(guid);
            if (response.getStatus() == Response.Status.NOT_FOUND) throw new UserNotFoundException();
            if (!response.isSuccessful()) throw new UserClientServiceFactoryException(response.getMessage());
            return response.getData();
        } catch (LithiumServiceClientFactoryException e) {
            throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

	public List<User> getUsers(List<String> guids) throws LithiumServiceClientFactoryException {
		List<User> users = new ArrayList<>();

		if(guids != null && guids.size() > 0) {
			Response<List<User>> response = getClient().getUsers(guids);
			users =  response.getData();
		}

		return users;
	}

    public User getUserById(Long userId) throws UserClientServiceFactoryException, UserNotFoundException {
        try {
            Response<User> response = getClient().getUserById(userId);
            if (response.getStatus() == Response.Status.NOT_FOUND) throw new UserNotFoundException();
            if (!response.isSuccessful()) throw new UserClientServiceFactoryException(response.getMessage());
            return response.getData();
        } catch (LithiumServiceClientFactoryException e) {
            throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

	public User performUserChecks(String userGuid, String locale, Long loginEventId, boolean checkEnabled,
	        boolean checkSessionTimeout, boolean checkTimeLimit) throws Status401UnAuthorisedException,
			Status405UserDisabledException, Status500UserInternalSystemClientException {
    	User user = null;
		try {
			Response<User> response = getClient().getUser(userGuid);
			log.debug("performUserChecks " + userGuid + " " + locale + " " + response);
			if (!response.isSuccessful()) {
				throw new Status500UserInternalSystemClientException("Unable to locate user " + userGuid);
			}
			user = response.getData();
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem retrieving user from svc-user | " + userGuid + " | " + e.getMessage());
			throw new Status500UserInternalSystemClientException(e);
		}
    	if (checkEnabled) {
			if (!user.getStatus().getUserEnabled()) {
				throw new Status405UserDisabledException(
						messageSource.getMessage("ERROR_DICTIONARY.LOGIN.USER_DISABLED",
						new Object[]{ new lithium.service.translate.client.objects.Domain(user.getDomain().getName())},
						"Your account is disabled.", LocaleContextHolder.getLocale()));
			}
		}
    	if (checkSessionTimeout) {
    		if (loginEventId == null) {
    			// In the unlikely case that login event id is null when it should be present,
			    // i.e checkSessionTimeout = true
    			throw new Status401UnAuthorisedException("Unable to validate session");
		    }
    		try {
    			getClient().validateAndUpdateSession(user.getDomain().getName(), loginEventId);
		    } catch (LithiumServiceClientFactoryException e) {
    			log.error("Problem trying to validateAndUpdateSession | user: " + user.guid()
					    + " loginEventId: " + loginEventId);
    			throw new Status500UserInternalSystemClientException(e);
		    }
	    }
    	return user;
	}

    public Map<String, String> guidsToUsernameMappings(Set<String> guids) throws Status500UserInternalSystemClientException {
        Map<String, String> guidsToUsernameMappings = new LinkedHashMap<>();
        Response<List<User>> response = null;
        try {
            List<String> guidsList = new ArrayList<>(guids);
            response = getClient().getUsers(guidsList);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem retrieving guidsToUsernameMappings from svc-user | " + e.getMessage());
            throw new Status500UserInternalSystemClientException(e);
        }
        if (!response.isSuccessful()) {
            String errorMsg = "User service returned an unhealthy response | " + response.getMessage();
            log.error(errorMsg + " | guids = " + Arrays.toString(guids.toArray()));
            throw new Status500UserInternalSystemClientException(errorMsg);
        }
        response.getData().stream().forEach(user -> {
            guidsToUsernameMappings.put(user.guid(), user.getUsername());
        });
        return guidsToUsernameMappings;
    }

    public User editUserVerificationStatus(UserVerificationStatusUpdate userVerificationStatusUpdate) throws UserClientServiceFactoryException, UserNotFoundException {
        try {
            Response<User> response = getClient().editUserVerificationStatus(userVerificationStatusUpdate);
            if (response.getStatus() == Response.Status.NOT_FOUND) throw new UserNotFoundException();
            if (!response.isSuccessful()) throw new UserClientServiceFactoryException(response.getMessage());
            return response.getData();
        } catch (LithiumServiceClientFactoryException e) {
            throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

    public String pushUserUpdateToPubSubUserService(String guid) throws UserClientServiceFactoryException, UserNotFoundException {
        try {
            ResponseEntity<String> response = factory.target(UserPubSubClient.class, true).pushToPubSub(guid);

            if (response.getStatusCodeValue() == Response.Status.NOT_FOUND.id()) throw new UserNotFoundException();
            if (response.getStatusCodeValue() != 200) throw new UserClientServiceFactoryException(response.getBody());
            return response.getBody();
        } catch (LithiumServiceClientFactoryException e) {
	        throw new UserClientServiceFactoryException("Error during call to service-user: " + ExceptionMessageUtil.allMessages(e), e);
        }
    }

	public User changeAccountStatus(final UserAccountStatusUpdate statusUpdate)
			throws LithiumServiceClientFactoryException {
		return getClient().changeAccountStatus(statusUpdate);
	}

	/**
	 * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
	 * things for the normal workflow.
	 */
	public void changeAccountStatusBasic(final UserAccountStatusUpdateBasic statusUpdate)
			throws Exception {
		Response<Boolean> response = getClient().changeAccountStatusBasic(statusUpdate);
		if (!response.isSuccessful()) {
			throw new Exception("Failed to change account status");
		}
	}

	public User updateProtectionOfCustomerFundsVersion(final String userGuid)
			throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
		return getClient().updateProtectionOfCustomerFundsVersion(userGuid);
	}

	public Response<String> updateOrAddIncompleteUserLabelValues(final String domainName, final String email,
	        final Map<String, String> additionalData) throws LithiumServiceClientFactoryException,
			UserNotFoundException {
    	return getClient().updateOrAddIncompleteUserLabelValues(domainName, email, additionalData);
	}

	public void logout(final String userGuid) throws LithiumServiceClientFactoryException {
    	getClient().logout(userGuid);
	}

	public void validateAndUpdateSession(final String domainName, final Long loginEventId)
			throws LithiumServiceClientFactoryException, Status401UnAuthorisedException {
    	getClient().validateAndUpdateSession(domainName, loginEventId);
	}

	private UserApiInternalClient getClient() throws LithiumServiceClientFactoryException {
		return factory.target(UserApiInternalClient.class, true);
	}

    private UserStatusClient getUserStatusClient() throws LithiumServiceClientFactoryException {
        return factory.target(UserStatusClient.class, true);
    }
	public User findByUsernameThenEmailThenCell(final String domain, final String usernameEmailOrCell) throws LithiumServiceClientFactoryException {
		return getClient().findByUsernameThenEmailThenCell(domain, usernameEmailOrCell).getData();
	}

	@Cacheable(key = "#root.args[0]", cacheNames = "lithium.service.user.client.service.user-full-name-by-guid", unless = "#result == null")
	public String getUserName(String guid) {
		try {
			String[] splittedGuid = guid.split("/");
			if (splittedGuid.length == 2) {
				User user = getUserByGuid(guid);
				return user.getName();
			}
		} catch (Throwable e) {
			log.error("Cant find user for guid=[" + guid + "]");
		}
		return guid;
	}

	public void blockIBANMismatchUser(String userGuid, String accoutNoteMessage, String financeNoteMessage) throws LithiumServiceClientFactoryException {
		getClient().blockIBANMismatchUser(userGuid, accoutNoteMessage, financeNoteMessage);
	}

	public Boolean isTestAccount(String userGuid) throws UserNotFoundException, UserClientServiceFactoryException {
		User user = getUserByGuid(userGuid);
		return user.getTestAccount();
	}

	public Response<User> updateVerificationStatus(boolean forceUpdate, UserVerificationStatusUpdate userVerificationStatusUpdate) throws Exception{
    	return getClient().updateVerificationStatus(forceUpdate, userVerificationStatusUpdate);
	}

	public Response<User> updateBiometricsStatus(UserBiometricsStatusUpdate userBiometricsStatusUpdate) throws Exception{
		return getClient().updateBiometricsStatus(userBiometricsStatusUpdate);
	}

	public Response<User> setTest(Long userId, boolean isTestAccount) throws Exception {
    	return getClient().setTest(userId, isTestAccount);
	}

	public Response<User> categoryAddPlayer(Long userId, String tagIds) throws Exception {
		List<Long> tagIdList = Stream.of(tagIds.split(","))
				.map(s -> s.trim())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		return getClient().categoryAddPlayer(userId, tagIdList);
	}

	public Response<User> categoryRemovePlayer(Long userId, String tagIds) throws Exception {
		List<Long> tagIdList = Stream.of(tagIds.split(","))
				.map(s -> s.trim())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		return getClient().categoryRemovePlayer(userId, tagIdList);
	}

	public Response<User> categoryRemoveAllPlayer(Long userId) throws Exception {
    	return getClient().categoryRemoveAllPlayer(userId);
	}

	public Response<User> setPromotionsOutOut(Long id, boolean optOut) throws Exception {
		return getClient().setPromotionsOptOut(id, optOut);
	}

	public SimplePageImpl<String> getUserGuidsWhosBirthdayIsToday (List<String> guids, int page, int limit) throws Exception{
		return getClient().getUserGuidsWhosBirthdayIsToday(page, limit, guids);
	}

    public List<Status> getAllUserStatuses() throws Exception {
        return getUserStatusClient().getAllUserStatuses().getData();
    }

    public List<StatusReason> getAllStatusReasons()  throws Exception{
        return getUserStatusClient().getAllStatusReasons().getData();
    }

		public User getLinkedEcosystemUserGuid(String userGuid, EcosystemRelationshipTypes relationshipType)
				throws Status500InternalServerErrorException {
			try {
				UserApiInternalClient client = getClient();
				Response<User> linkedEcosystemUser = client.getLinkedEcosystemUserGuid(userGuid,
						relationshipType);
				if (!linkedEcosystemUser.isSuccessful()) { throw new LithiumServiceClientFactoryException(linkedEcosystemUser.getMessage()); }
				User user = linkedEcosystemUser.getData();
				if (ObjectUtils.isEmpty(user)) {
					return null;
				}
				return user;
			} catch (LithiumServiceClientFactoryException e) {
				throw new Status500InternalServerErrorException("Unable to getLinkedEcosystemUserGuid for userGuid=" + userGuid + ", message=" + e.getMessage(), e);
			}
		}
}
