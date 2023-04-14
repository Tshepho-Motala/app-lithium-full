package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status489PlayerExclusionNotFoundException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerExclusionHistory;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.repositories.PlayerExclusionHistoryRepository;
import lithium.service.limit.data.repositories.PlayerExclusionV2Repository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdateBasic;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExclusionService {
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private CoolOffService coolOffService;
	@Autowired private ExclusionPlayerCommsService playerCommsService;
	@Autowired private PlayerExclusionHistoryRepository historyRepository;
	@Autowired private PlayerExclusionV2Repository repository;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private UserRestrictionService userRestrictionService;
	@Autowired private LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired private RestrictionService restrictionService;
	@Autowired @Setter MessageSource messageSource;

	public static final String DOMAIN_SETTING_EXCLUSION_PERIODS_IN_MONTHS = "exclusion-periods-in-months";
	public static final String DEFAULT_EXCLUSION_PERIODS_IN_MONTHS = "6,12,24,60";

	// TODO: Don't really like having provider specific code in here.
	//       Figure out a way to clean this up.
	public static final String EXCLUSION_ADVISOR_GAMSTOP = "GAMSTOP";
	public static final String EXCLUSION_ADVISOR_CRUKS = "CRUKS";

	public List<Integer> getExclusionPeriodsInMonths(String domainName) throws Status550ServiceDomainClientException {
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DOMAIN_SETTING_EXCLUSION_PERIODS_IN_MONTHS);
		if (setting.isPresent()) {
			return parseExclusionPeriodsInMonthsSetting(setting.get());
		} else {
			return parseExclusionPeriodsInMonthsSetting(DEFAULT_EXCLUSION_PERIODS_IN_MONTHS);
		}
	}

	private List<Integer> parseExclusionPeriodsInMonthsSetting(String value) {
		List<Integer> periodsInMonths = new ArrayList<>();
		String[] settings = value.split(",");
		for (String setting: settings) {
			try {
				Integer periodInMonths = Integer.parseInt(setting.trim());
				periodsInMonths.add(periodInMonths);
			} catch (NumberFormatException nfe) {
				log.warn("Could not parse (" + setting + ") due to " + nfe.getMessage() + ". The value is ignored.");
			}
		}
		return periodsInMonths;
	}

	public PlayerExclusionV2 lookup(String playerGuid) {
		return repository.findByPlayerGuid(playerGuid);
	}

	public PlayerExclusionV2 set(String playerGuid, Integer periodInMonths, String authorGuid, ExclusionSource exclusionSource,
			String exclusionAdvisor, Date exclusionExpiryDate, LithiumTokenUtil tokenUtil) throws UserNotFoundException, UserClientServiceFactoryException, Status500InternalServerErrorException, Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException, LithiumServiceClientFactoryException, Status409PlayerRestrictionConflictException, Status403PlayerRestrictionDeniedException, Status422PlayerRestrictionExclusionException {
		User player = userApiInternalClientService.getUserByGuid(playerGuid);
		return set(player, periodInMonths, authorGuid, exclusionSource, exclusionAdvisor, exclusionExpiryDate, tokenUtil);
	}

	private String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}

	@Transactional(rollbackFor = Exception.class)
	public PlayerExclusionV2 set(User player, Integer periodInMonths, String authorGuid, ExclusionSource exclusionSource,
			String exclusionAdvisor, Date exclusionExpiryDate, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException, Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException, LithiumServiceClientFactoryException, Status409PlayerRestrictionConflictException, Status403PlayerRestrictionDeniedException, Status422PlayerRestrictionExclusionException {
		log.debug("ExclusionService.set [player="+player+", periodInMonths="+periodInMonths+", authorGuid="+authorGuid
			+ ", exclusionSource="+exclusionSource+", exclusionAdvisor="+exclusionAdvisor
			+ ", exclusionExpiryDate="+exclusionExpiryDate+"]");

		PlayerExclusionV2 playerExclusion = lookup(player.guid());

		if (playerExclusion != null && playerExclusion.isPermanent()) {
			throw new Status491PermanentSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PERMANENT_SELF_EXCLUSION_SET", new Object[]{new lithium.service.translate.client.objects.Domain(player.getDomain().getName())}, "Player is permanently self excluded", LocaleContextHolder.getLocale()));
		}

		DomainRestrictionSet restriction = restrictionService.findByDomainAndName(player.getDomain().getName(),
				SystemRestriction.LOGIN_BLOCK_AFTER_SE.restrictionName());

		if (playerExclusion != null) {
			// More Gamstop specific code, might need to find a better control structure in future
			if ((playerExclusion.getAdvisor() != null && playerExclusion.getAdvisor().contentEquals(EXCLUSION_ADVISOR_GAMSTOP)) &&
					(exclusionAdvisor != null && exclusionAdvisor.contentEquals(EXCLUSION_ADVISOR_GAMSTOP)) ) {
				playerExclusion.setExpiryDate(exclusionExpiryDate);
				playerExclusion = repository.save(playerExclusion);

				addHistory(playerExclusion, ModifyType.UPDATED, authorGuid);

				// No need to continue with all the fancy-pants stuff in here after this point.
				return playerExclusion;
			}

			String msg = null;
			if (playerExclusion.getExpiryDate().after(new Date())) {
				msg = "Player is soft excluded. Ends " + playerExclusion.getExpiryDateDisplay();
				throw new Status490SoftSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.SOFT_SELF_EXCLUSION",  new Object[]{new lithium.service.translate.client.objects.Domain(player.getDomain().getName())}, msg, LocaleContextHolder.getLocale()));
			}
		}

		// If we fail to remove the cooling off (if present), we cannot proceed adding in the SE.
		coolOffService.systemClear(player, "System CO removal due to SE being applied");

		Date expiryDate = null;
		if (exclusionExpiryDate != null) {
			// Added for gamstop. Just using whatever date is passed in.
			expiryDate = exclusionExpiryDate;
		} else if (periodInMonths != null) {
			expiryDate = DateTime.now().plusMonths(periodInMonths).toDate();
		}
		// If we get to this point and expiry date is still null, means this is a permanent exclusion

		// Forcing a delete because we want to add a new one if the player status is open
		if (player.getStatus().getName().contentEquals(Status.OPEN.statusName())) {
			repository.deleteByPlayerGuid(player.guid());
			try {
				userRestrictionService.lift(player.guid(), restriction, authorGuid, "Restriction Stringency Increase", player.getId(), null);
			} catch (Status403PlayerRestrictionDeniedException | Status409PlayerRestrictionConflictException e) {
				log.error(e.getMessage(), e);
				throw e;
			}

		}
		log.debug("expiryDate " + expiryDate);
		playerExclusion = repository.save(PlayerExclusionV2.builder().playerGuid(player.guid())
			.expiryDate(expiryDate).permanent((expiryDate == null))
			.exclusionSource((exclusionSource != null)? exclusionSource: ExclusionSource.INTERNAL)
			.advisor(exclusionAdvisor).build());

		// Reason for user status update to frozen
		StatusReason statusReason = userStatusReasonFromAdvisor(exclusionAdvisor);

		SubCategory subCat = SubCategory.fromName(statusReason.description());
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(playerExclusion, new PlayerExclusionV2(),
				new String[] {"playerGuid", "createdDate", "expiryDate", "permanent", "exclusionSource", "advisor"});
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "create", player.getId(), authorGuid,
					tokenUtil, null,null, clfc, Category.RESPONSIBLE_GAMING, subCat, 80, player.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for player exclusion create failed";
			log.error(msg + " [playerGuid="+player.guid()+", authorGuid="+authorGuid+"]" + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}

		try {
			userRestrictionService.place(player.guid(), restriction, User.SYSTEM_GUID,
				"User restriction set by system due to " + statusReason.statusReasonName(), player.getId(),null, null);
		} catch (Status403PlayerRestrictionDeniedException | Status409PlayerRestrictionConflictException | Status422PlayerRestrictionExclusionException e) {
			log.error(e.getMessage(), e);
			throw e;
		}

		userApiInternalClientService.changeAccountStatus(
			UserAccountStatusUpdate.builder()
			.userGuid(player.guid())
			.statusName(Status.FROZEN.statusName())
			.statusReasonName(statusReason.statusReasonName())
			.comment("User account status updated by system due to " + statusReason.statusReasonName())
			.selfExcluded(true)
			.selfExclusionPermanent((expiryDate == null))
			.selfExclusionCreated(formatDate(new Date()))
			.selfExclusionExpiry((expiryDate!=null)?formatDate(expiryDate):null)
			.noteCategoryName(Category.ACCOUNT.getName())
			.noteSubCategoryName(SubCategory.STATUS_CHANGE.getName())
			.build()
		);

		try {
			getUserApiInternalClient().markHasSelfExcludedAndOptOutComms(player.guid());
		} catch (Exception e) {
			log.warn("Player self exclusion set successfully, but failed to mark player has self excluded flag and opt out of comms in " +
				"svc-user | " + e.getMessage() + " [playerGuid="+player.guid()+", authorGuid="+authorGuid+"]", e);
		}

		addHistory(playerExclusion, ModifyType.CREATED, authorGuid);

		playerCommsService.communicateWithPlayerV2(player, playerExclusion);

		return playerExclusion;
	}

	/**
	 * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
	 * things for the normal workflow.
	 */
	@Transactional(rollbackFor = Exception.class)
	public PlayerExclusionV2 setMinimal(String domainName, String playerGuid, Date createdDate, Date expiryDate,
			String advisor, ExclusionSource exclusionSource) throws Exception {
		log.trace("Received request to set minimal player exclusion | domainName: {}, playerGuid: {},"
				+ " createdDate: {}, expiryDate: {}, advisor: {}, exclusionSource: {}", domainName, playerGuid,
				createdDate, expiryDate, advisor, exclusionSource);
		PlayerExclusionV2 playerExclusion = lookup(playerGuid);
		log.trace("Player exclusion lookup returned | {}", playerExclusion);

		// Not considering duplicates
		if (playerExclusion == null) {
			// If cool off is present, remove it. Self exclusion takes precedence.
			// The account status will be changed below.
			coolOffService.systemClearBasic(playerGuid);

			playerExclusion = repository.save(
					PlayerExclusionV2.builder()
							.createdDate(createdDate)
							.playerGuid(playerGuid)
							.expiryDate(expiryDate)
							.permanent(expiryDate == null)
							.exclusionSource(exclusionSource)
							.advisor(advisor)
							.build()
			);
			log.trace("Saved player exclusion | {}", playerExclusion);

			DomainRestrictionSet restriction = restrictionService.findByDomainAndName(domainName,
					SystemRestriction.LOGIN_BLOCK_AFTER_SE.restrictionName());
			UserRestrictionSet userRestriction = userRestrictionService.placeBasicRemoveDuplicate(playerGuid,
					restriction);
			log.trace("Saved user restriction | {}", userRestriction);

			userApiInternalClientService.changeAccountStatusBasic(
					UserAccountStatusUpdateBasic.builder()
							.userGuid(playerGuid)
							.statusName(Status.FROZEN.statusName())
							.statusReasonName(StatusReason.SELF_EXCLUSION.statusReasonName())
							.markSelfExcluded(true)
							.optOutComms(true)
							.build()
			);
			log.trace("Changed player account status");
		}

		return playerExclusion;
	}

	private StatusReason userStatusReasonFromAdvisor(String advisor) {
		if (advisor == null || advisor.isEmpty()) return StatusReason.SELF_EXCLUSION;
		switch (advisor) {
			case EXCLUSION_ADVISOR_CRUKS: return StatusReason.CRUKS_SELF_EXCLUSION;
			case EXCLUSION_ADVISOR_GAMSTOP: return StatusReason.GAMSTOP_SELF_EXCLUSION;
			default: return StatusReason.SELF_EXCLUSION;
		}
	}

	public void clear(String playerGuid, String authorGuid, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException,
			UserNotFoundException, UserClientServiceFactoryException,
			Status489PlayerExclusionNotFoundException, LithiumServiceClientFactoryException {
		User player = userApiInternalClientService.getUserByGuid(playerGuid);
		clear(player, authorGuid, tokenUtil);
	}

	public void clear(User player, String authorGuid, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException,
			Status489PlayerExclusionNotFoundException, LithiumServiceClientFactoryException {
		PlayerExclusionV2 playerExclusion = lookup(player.guid());
		boolean isGamstop = false;
		if (player.getStatusReason() != null) {
			isGamstop = StatusReason.GAMSTOP_SELF_EXCLUSION.name().contentEquals(player.getStatusReason().getName());
		} else if (playerExclusion != null && playerExclusion.getAdvisor() != null) {
			isGamstop =  playerExclusion.getAdvisor().contentEquals(EXCLUSION_ADVISOR_GAMSTOP);
		}

		if (playerExclusion == null && !isGamstop) {
			throw new Status489PlayerExclusionNotFoundException();
		}

		addHistory(playerExclusion, ModifyType.REMOVED, authorGuid);

		repository.deleteByPlayerGuid(player.guid());

		StatusReason statusReason = StatusReason.SELF_EXCLUSION;
		if (isGamstop) {
			if (playerExclusion != null) {
					if (playerExclusion.getAdvisor() != null &&
							playerExclusion.getAdvisor().contentEquals(EXCLUSION_ADVISOR_GAMSTOP)) {
						statusReason = StatusReason.GAMSTOP_SELF_EXCLUSION;
					}
			} else {
				statusReason = StatusReason.GAMSTOP_SELF_EXCLUSION;
			}
		}

		if ((playerExclusion == null && isGamstop) || playerExclusion != null) {
			if (tokenUtil != null) {
				UserAccountStatusUpdate userAccountStatusUpdate = UserAccountStatusUpdate.builder()
						.userGuid(player.guid())
						.statusName(Status.OPEN.statusName())
						.comment("User account status updated by system due to " + statusReason.statusReasonName() + " being cleared.")
						.selfExcluded(false)
						.build();
				userApiInternalClientService.changeAccountStatus(userAccountStatusUpdate);
			}
		}

		if (playerExclusion != null) {
			try {
				List<ChangeLogFieldChange> clfc = changeLogService.compare(new PlayerExclusionV2(), playerExclusion,
						new String[]{"playerGuid", "createdDate", "expiryDate", "permanent"});
				changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "delete", player.getId(), authorGuid,
						tokenUtil, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.SELF_EXCLUSION, 70, player.getDomain().getName());
			} catch (Exception e) {
				String msg = "Changelog registration for player exclusion delete failed";
				log.error(msg + " [playerGuid=" + player.guid() + ", authorGuid=" + authorGuid + "]" + e.getMessage(), e);
				throw new Status500InternalServerErrorException(msg);
			}

			playerCommsService.communicateWithPlayerV2(player, null);
		}
	}

	private void addHistory(PlayerExclusionV2 playerExclusionV2, ModifyType modifyType, String authorGuid) {
		PlayerExclusionHistory peh = historyRepository.save(
			PlayerExclusionHistory.builder()
			.createdDate(playerExclusionV2.getCreatedDate())
			.expiryDate(playerExclusionV2.getExpiryDate())
			.permanent(playerExclusionV2.isPermanent())
			.playerGuid(playerExclusionV2.getPlayerGuid())
			.advisor(playerExclusionV2.getAdvisor())
			.exclusionSource(playerExclusionV2.getExclusionSource())
			.modifyType(modifyType)
			.modifyAuthorGuid(authorGuid)
			.build()
		);
		log.debug("Saved player exclusion history:: " + peh);
	}

	private UserApiInternalClient getUserApiInternalClient() throws LithiumServiceClientFactoryException {
		UserApiInternalClient client = lithiumServiceClientFactory.target(UserApiInternalClient.class,
			"service-user", true);
		return client;
	}
}
