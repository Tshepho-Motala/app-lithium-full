package lithium.service.cashier.services;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUser;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorUserRepository;
import lithium.service.cashier.data.repositories.UserCategoriesRepository;
import lithium.service.cashier.data.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.domain.client.DomainClient;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.UserCategoryClient;
import lithium.service.user.client.objects.UserApiToken;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.objects.UserCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LimitsService limitsService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	LithiumServiceClientFactory serviceFactory;
	@Autowired
	private ProcessorUserRepository processorUserRepository;
	@Autowired
	private LeaderCandidate leaderCandidate;

    @Autowired
    private UserCategoriesRepository categoriesRepository;

	public User createOrUpdate(String userGuid, Long limitsId, Long profileId) {
		log.debug("Create/Update User : " + userGuid);
		User user = userRepository.findByGuid(userGuid);
		if (user == null) user = User.builder().guid(userGuid).build();

		user.setLimits(limitsService.find(limitsId));
		user.setProfile(profileService.find(profileId));

		return userRepository.save(user);
	}

	public User createOrUpdate(String userGuid, Long profileId) throws Exception {
		log.debug("Create/Update User : " + userGuid);
		User user = userRepository.findByGuid(userGuid);
		if (user == null) user = User.builder().guid(userGuid).build();

		Profile profile = profileService.find(profileId);
		if ((profile != null) && (!profile.getDomain().getName().equalsIgnoreCase(user.domainName()))) {
			log.error("Tried to link : " + userGuid + " to " + profile);
			throw new Exception("Invalid profile specified.");
		}

		user.setProfile(profile);

		return userRepository.save(user);
	}

	public User find(String userGuid) {
		return userRepository.findByGuid(userGuid);
	}

	public User findOrCreate(String userGuid) {
		log.debug("Find/Create User : " + userGuid);
		User user = userRepository.findByGuid(userGuid);
		if (user == null) user = userRepository.save(User.builder().guid(userGuid).build());
		return user;
	}

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 50))
	public User findOrCreateRetryable(String userGuid) {
		return findOrCreate(userGuid);
	}


	public Page<User> findByProfile(DataTableRequest request, Profile profile) {
		return userRepository.findByProfile(profile, request.getPageRequest());
	}

	public User createLimits(
			String userGuid,
			Long minAmount,
			Long maxAmount,
			Long maxAmountDay,
			Long maxAmountWeek,
			Long maxAmountMonth,
			Long maxTransactionsDay,
			Long maxTransactionsWeek,
			Long maxTransactionsMonth,
			Long minFirstTransactionAmount,
			Long maxFirstTransactionAmount
	) {
		User user = findOrCreate(userGuid);
		user.setLimits(limitsService.create(minAmount, maxAmount, minFirstTransactionAmount, maxFirstTransactionAmount, maxAmountDay, maxAmountWeek, maxAmountMonth, maxTransactionsDay, maxTransactionsWeek, maxTransactionsMonth));
		return userRepository.save(user);
	}

	public User removeLimits(
			String userGuid
	) {
		User user = findOrCreate(userGuid);
		log.debug("Removing limits from : " + user);
		limitsService.delete(user.getLimits().getId());
		user.setLimits(null);
		return userRepository.save(user);
	}

	@Cacheable(value = "lithium.service.domain.data.findbyname", key = "#root.args[0]", unless = "#result == null")
	public lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService(String domainName) throws Exception {
		DomainClient domainClient = serviceFactory.target(DomainClient.class, "service-domain", true);
		Response<lithium.service.domain.client.objects.Domain> domain = domainClient.findByName(domainName);
		if (domain.isSuccessful() && domain.getData() != null) {
			return domain.getData();
		}
		throw new DoErrorException("Unable to retrieve domain from domain service " + domainName);
	}

	public lithium.service.user.client.objects.User retrieveUserFromUserService(User cashierUser) throws Exception {
		UserApiInternalClient userClient = serviceFactory.target(UserApiInternalClient.class);
		Response<lithium.service.user.client.objects.User> user = userClient.getUser(cashierUser.getGuid());
		if (user.isSuccessful() && user.getData() != null) {
			user.getData().setLabelAndValue(userClient.findUserLabelValues(cashierUser.getGuid()).getData());
			//Perform a safe lookup for api dat not sent in normal user request
			try {
				UserApiClient userApiClient = serviceFactory.target(UserApiClient.class);
				Response<UserApiToken> userApiToken = userApiClient.getApiTokenByUserGuid(cashierUser.getGuid());
				if (userApiToken.isSuccessful() && userApiToken.getData() != null) {
					lithium.service.user.client.objects.User u = user.getData();
					u.setApiToken(userApiToken.getData().getToken());
					u.setShortGuid(userApiToken.getData().getShortGuid());
					return u;
				}
			} catch (Exception ex) {
				log.warn("Unable to lookup user API token data: " + cashierUser.getGuid());
			}
			return user.getData();
		}
		throw new DoErrorException("Unable to retrieve user from user service " + user.toString());
	}

	/**
	 * Some of the processors assign identifiers to users when they deposit and require those identifiers when a withdrawal is requested
	 *
	 * @param processorUserId
	 * @param user
	 * @param domainMethodProcessor
	 * @return
	 */
	public ProcessorUser findOrCreateProcessorUser(String processorUserId, User user, DomainMethodProcessor domainMethodProcessor) {
		if (processorUserId == null || processorUserId.isEmpty() || user == null || domainMethodProcessor == null)
			return null;

		ProcessorUser processorUser = processorUserRepository.findByUserAndDomainMethodProcessor(user, domainMethodProcessor);
		if (processorUser == null) {
			processorUser = ProcessorUser.builder().user(user).domainMethodProcessor(domainMethodProcessor).processorUserId(processorUserId).build();
			processorUser = processorUserRepository.save(processorUser);
		}
		return processorUser;
	}

	public ProcessorUser findProcessorUser(User user, DomainMethodProcessor domainMethodProcessor) {
		if (user == null || domainMethodProcessor == null) return null;

		ProcessorUser processorUser = processorUserRepository.findByUserAndDomainMethodProcessor(user, domainMethodProcessor);

		return processorUser;
	}

	public List<String> findUserPlayerTagNames(Long userId) throws Exception {
		UserCategoryClient userPlayersClient = serviceFactory.target(UserCategoryClient.class, "service-user", true);
		List<UserCategory> userPlayerTags = userPlayersClient.getUserCategoriesOfUser(userId);
		return userPlayerTags.stream().map(UserCategory::getName).collect(Collectors.toList());
	}

	public List<UserCategory> getDomainUserCategories(String domainName) throws Exception {
		UserCategoryClient userPlayersClient = serviceFactory.target(UserCategoryClient.class, "service-user", true);
		return userPlayersClient.getDomainUserCategories(domainName);
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		user.setCreatedDate(data.getCreatedDate());
        user.setStatusId(data.getStatusId());
		userRepository.save(user);
        processUserTags(user, data.getPlayerTagIds());
	}

    private void processUserTags(User user,  Collection<Long> expectedTagIds) {
        List<lithium.service.cashier.data.entities.UserCategory> userTags = new ArrayList<>();
        expectedTagIds.forEach(
                tagId -> userTags.add(findOrCreateTag(user, tagId))
        );
        user.setUserCategories(userTags);
        userRepository.save(user);
    }

    private lithium.service.cashier.data.entities.UserCategory findOrCreateTag(User user, Long tagId) {
        return categoriesRepository.findByUserAndUserCategoryId(user, tagId)
                .orElseGet(() ->  lithium.service.cashier.data.entities.UserCategory.builder()
                        .user(user)
                        .userCategoryId(tagId)
                        .build()
                );
    }

    public User getSystemUser() {
		return findOrCreate(lithium.service.user.client.objects.User.SYSTEM_GUID);
	}
}
