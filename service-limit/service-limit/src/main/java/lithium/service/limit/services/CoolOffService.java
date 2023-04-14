package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status497PlayerCoolingOffNotFoundException;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerCoolOffHistory;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.data.repositories.PlayerCoolOffHistoryRepository;
import lithium.service.limit.data.repositories.PlayerCoolOffRepository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdateBasic;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
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
public class CoolOffService {
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private CoolOffPlayerCommsService playerCommsService;
	@Autowired private ExclusionService exclusionService;
	@Autowired private PlayerCoolOffRepository repository;
	@Autowired private PlayerCoolOffHistoryRepository historyRepository;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private MessageSource messageSource;

	public static final String DOMAIN_SETTING_COOL_OFF_PERIODS_IN_DAYS = "cool-off-periods-in-days";
	public static final String DEFAULT_COOL_OFF_PERIODS_IN_DAYS = "1,7,14,21,28,42";

	public List<Integer> getCooloffPeriodsInDays(String domainName) throws Status550ServiceDomainClientException {
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DOMAIN_SETTING_COOL_OFF_PERIODS_IN_DAYS);
		if (setting.isPresent()) {
			return parseCooloffPeriodsInDaysSetting(setting.get());
		} else {
			return parseCooloffPeriodsInDaysSetting(DEFAULT_COOL_OFF_PERIODS_IN_DAYS);
		}
	}

	private List<Integer> parseCooloffPeriodsInDaysSetting(String value) {
		List<Integer> periodsInDays = new ArrayList<>();
		String[] settings = value.split(",");
		for (String setting: settings) {
			try {
				Integer periodInDays = Integer.parseInt(setting.trim());
				periodsInDays.add(periodInDays);
			} catch (NumberFormatException nfe) {
				log.warn("Could not parse (" + setting + ") due to " + nfe.getMessage() + ". The value is ignored.");
			}
		}
		return periodsInDays;
	}

	public PlayerCoolOff lookup(String playerGuid) {
		return repository.findByPlayerGuid(playerGuid);
	}

	public PlayerCoolOff set(String playerGuid, int periodInDays, String authorGuid, LithiumTokenUtil tokenUtil)
			throws UserNotFoundException, UserClientServiceFactoryException,
			Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, Status500InternalServerErrorException,
			LithiumServiceClientFactoryException {
		User player = userApiInternalClientService.getUserByGuid(playerGuid);
		return set(player, periodInDays, authorGuid, tokenUtil);
	}

	private String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}

	@Transactional(rollbackFor = Exception.class)
	public PlayerCoolOff set(User player, int periodInDays, String authorGuid, LithiumTokenUtil tokenUtil)
			throws Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, Status500InternalServerErrorException,
			LithiumServiceClientFactoryException {
		PlayerCoolOff playerCoolingOff = lookup(player.guid());
		if (playerCoolingOff != null && playerCoolingOff.getExpiryDate().after(new Date()))
			throw new Status496PlayerCoolingOffException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.FLAGGED_AS_COOLING_OFF_EXPIRES_ON", new Object[]{new lithium.service.translate.client.objects.Domain(playerCoolingOff.getPlayerGuid().split("/")[0]), playerCoolingOff.getExpiryDateDisplay()}, "Player account is flagged as cooling off. Expires on {0}.", LocaleContextHolder.getLocale()));

		// Cooling off cannot be applied if the player is self excluded.
		PlayerExclusionV2 playerExclusionV2 = exclusionService.lookup(player.guid());
		if (playerExclusionV2 != null) {
			if (playerExclusionV2.isPermanent()) {
				throw new Status491PermanentSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PERMANENT_SELF_EXCLUSION_SET",
						new Object[]{new lithium.service.translate.client.objects.Domain(playerExclusionV2.getPlayerGuid().split("/")[0]),
								playerExclusionV2.getExpiryDateDisplay()}, "Player is permanently self excluded. Expires on {0}",
						LocaleContextHolder.getLocale()));
			} else {
				throw new Status490SoftSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.SOFT_SELF_EXCLUSION",
						new Object[]{new lithium.service.translate.client.objects.Domain(playerExclusionV2.getPlayerGuid().split("/")[0]),
								playerExclusionV2.getExpiryDateDisplay()}, "Player is soft excluded. Expires on {0}",
						LocaleContextHolder.getLocale()));
			}
		}

		Date expiryDate = DateTime.now().plusDays(periodInDays).toDate();
		log.debug("expiryDate " + expiryDate);
		PlayerCoolOff playerCoolOff = repository.save(PlayerCoolOff.builder().playerGuid(player.guid())
			.expiryDate(expiryDate).build());

		userApiInternalClientService.changeAccountStatus(
			UserAccountStatusUpdate.builder()
			.userGuid(player.guid())
			.statusName(Status.FROZEN.statusName())
			.statusReasonName(StatusReason.COOLING_OFF.statusReasonName())
			.comment("User account status updated by system due to " + StatusReason.COOLING_OFF.statusReasonName())
			.coolingOff(true)
			.coolingOffCreated(formatDate(new Date()))
			.coolingOffExpiry(formatDate(expiryDate))
			.build());

		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(playerCoolOff, new PlayerCoolOff(),
				new String[] {"playerGuid", "createdDate", "expiryDate"});
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.cooloff", "create", player.getId(), authorGuid,
				tokenUtil,null, null, clfc, Category.ACCOUNT, SubCategory.RESPONSIBLE_GAMING, 0, player.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for player cool off create failed";
			log.error(msg + " [playerGuid="+player.guid()+", "+ ", periodInDays="+periodInDays
				+ ", authorGuid="+authorGuid+"]" + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}

		addHistory(playerCoolOff, ModifyType.CREATED, authorGuid);

		playerCommsService.communicateWithPlayer(player, playerCoolOff);

		return playerCoolOff;
	}

	/**
	 * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
	 * things for the normal workflow.
	 */
	@Transactional(rollbackFor = Exception.class)
	public PlayerCoolOff setMinimal(String playerGuid, Date createdDate, Date expiryDate) throws Exception {
		log.trace("Received request to set minimal player cool off | playerGuid: {}, createdDate: {},"
						+ " expiryDate: {}", playerGuid, createdDate, expiryDate);
		PlayerCoolOff playerCoolingOff = lookup(playerGuid);
		log.trace("Player cool off lookup returned | {}", playerCoolingOff);

		// Not considering duplicates
		if (playerCoolingOff == null) {
			// Player exclusion takes precedence. If it is, ignore this request.
			PlayerExclusionV2 playerExclusionV2 = exclusionService.lookup(playerGuid);
			log.trace("Player exclusion lookup returned | {}", playerExclusionV2);
			if (playerExclusionV2 != null) {
				return null;
			}

			playerCoolingOff = repository.save(
					PlayerCoolOff.builder()
							.playerGuid(playerGuid)
							.createdDate(createdDate)
							.expiryDate(expiryDate)
							.build()
			);
			log.trace("Saved player cool off | {}", playerCoolingOff);

			userApiInternalClientService.changeAccountStatusBasic(
					UserAccountStatusUpdateBasic.builder()
							.userGuid(playerGuid)
							.statusName(Status.FROZEN.statusName())
							.statusReasonName(StatusReason.COOLING_OFF.statusReasonName())
							.markSelfExcluded(false)
							.optOutComms(false)
							.build()
			);
			log.trace("Changed player account status");
		}

		return playerCoolingOff;
	}

	public void clear(String playerGuid, String authorGuid, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException,
			Status497PlayerCoolingOffNotFoundException, UserNotFoundException, UserClientServiceFactoryException,
			LithiumServiceClientFactoryException {
		User player = userApiInternalClientService.getUserByGuid(playerGuid);
		clear(player, authorGuid, tokenUtil);
	}

	public void clear(User player, String authorGuid, LithiumTokenUtil tokenUtil) throws Status497PlayerCoolingOffNotFoundException,
			Status500InternalServerErrorException, LithiumServiceClientFactoryException {
		PlayerCoolOff playerCoolOff = lookup(player.guid());
		if (playerCoolOff == null)
			throw new Status497PlayerCoolingOffNotFoundException();

		addHistory(playerCoolOff, ModifyType.REMOVED, authorGuid);

		repository.deleteByPlayerGuid(player.guid());

		if (player.getStatus().getName().equals(Status.FROZEN.statusName())) {
			if(player.getStatusReason().getName().equals(StatusReason.SELF_EXCLUSION.statusReasonName())){
				// do nothing
			}else {
				userApiInternalClientService.changeAccountStatus(
						UserAccountStatusUpdate.builder()
								.userGuid(player.guid())
								.statusName(Status.OPEN.statusName())
								.comment("User account status updated by system due to "
										+ StatusReason.COOLING_OFF.statusReasonName()
										+ " being cleared.")
								.coolingOff(false)
								.build());
			}
		}

		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(new PlayerCoolOff(), playerCoolOff,
					new String[]{"playerGuid", "createdDate", "expiryDate"});
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.cooloff", "delete", player.getId(), authorGuid,
					tokenUtil, null, null, clfc, Category.ACCOUNT, SubCategory.RESPONSIBLE_GAMING, 0, player.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for player cool off delete failed";
			log.error(msg + " [playerGuid="+player.guid()+", authorGuid="+authorGuid+"]" + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}

		playerCommsService.communicateWithPlayer(player, null);
	}

	// Account status will be changed when adding in the SE, for now, just removing the CO.
	@Transactional(rollbackFor = Exception.class)
	public void systemClear(User player, String reason) throws Status500InternalServerErrorException {
		PlayerCoolOff playerCoolOff = lookup(player.guid());
		if (playerCoolOff != null) {
			addHistory(playerCoolOff, ModifyType.REMOVED, "SYSTEM");
			repository.deleteByPlayerGuid(player.guid());
			try {
				List<ChangeLogFieldChange> clfc = changeLogService.compare(new PlayerCoolOff(), playerCoolOff,
						new String[]{"playerGuid", "createdDate", "expiryDate"});
				changeLogService.registerChangesWithDomain("user.cooloff", "delete", player.getId(),
						"SYSTEM", reason, null, clfc, Category.ACCOUNT,
						SubCategory.RESPONSIBLE_GAMING, 0, player.getDomain().getName());
			} catch (Exception e) {
				String msg = "Changelog registration for player cool off system delete failed";
				log.error(msg + " [playerGuid="+player.guid()+"]" + e.getMessage(), e);
				throw new Status500InternalServerErrorException(msg);
			}
		}
	}

	/**
	 * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
	 * things for the normal workflow.
	 */
	@Transactional(rollbackFor = Exception.class)
	public void systemClearBasic(String playerGuid) {
		PlayerCoolOff playerCoolOff = lookup(playerGuid);
		if (playerCoolOff != null) {
			repository.deleteByPlayerGuid(playerGuid);
		}
	}

	private void addHistory(PlayerCoolOff playerCoolOff, ModifyType modifyType, String authorGuid) {
		PlayerCoolOffHistory coh = historyRepository.save(
			PlayerCoolOffHistory.builder()
			.createdDate(playerCoolOff.getCreatedDate())
			.expiryDate(playerCoolOff.getExpiryDate())
			.playerGuid(playerCoolOff.getPlayerGuid())
			.modifyType(modifyType)
			.modifyAuthorGuid(authorGuid)
			.build()
		);
		log.debug("Saved player cool off history:: " + coh);
	}
}
