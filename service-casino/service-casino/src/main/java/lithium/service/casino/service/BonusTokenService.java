package lithium.service.casino.service;

import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.client.objects.PlayerBonusTokenStatus;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusToken;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.repositories.BonusTokenRepository;
import lithium.service.casino.data.repositories.PlayerBonusTokenRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//TODO: Add state transition logging and debug logging to the flows, also some timing maybe

/**
 * Management of bonus tokens (the token is linked to a bonus revision and is stateless)<br>
 * and management of player bonus tokens that is derived from a bonus token and linked to a player (has state management)
 */
@Slf4j
@AllArgsConstructor
@Service
public class BonusTokenService {

	public static final String BONUS_TOKEN_DEFAULT_MINIMUM_ODDS_NAME = "bonusTokenDefaultMinimumOdds";
	public static final String BONUS_TOKEN_DEFAULT_MINIMUM_TOKEN_VALUE_NAME = "bonusTokenDefaultMinimumTokenValue";
	public static final String BONUS_TOKEN_DEFAULT_MAXIMUM_TOKEN_VALUE_NAME = "bonusTokenDefaultMaximumTokenValue";
	public static final String BONUS_TOKEN_DEFAULT_EXPIRY_DURATION_DAYS_NAME = "bonusTokenDefaultExpiryDurationDaysValue";

	private PlayerBonusTokenRepository playerBonusTokenRepository;
	private UserRepository userRepository;
	private BonusTokenRepository bonusTokenRepository;
	private CachingDomainClientService cachingDomainClientService;
	private DomainService domainService;

	public List<PlayerBonusToken> handlePlayerTokenLookup(String playerGuid) {
		List<lithium.service.casino.data.entities.PlayerBonusToken> tokenList =
				playerBonusTokenRepository.findByUserAndStatus(
						userRepository.findOrCreateByGuid(playerGuid, User::new),
						PlayerBonusTokenStatus.ACTIVE.code());

		return tokenList
				.stream()
				.map(BonusTokenService::playerBonusTokenMapper)
				.collect(Collectors.toList());
	}

	public PlayerBonusToken handleBonusTokenValidation(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException {

				return playerBonusTokenMapper(handleBonusTokenValidationInternal(playerGuid, bonusTokenId));

	}

	public PlayerBonusToken reserveBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		lithium.service.casino.data.entities.PlayerBonusToken entity =
				handleBonusTokenValidationInternal(playerGuid, bonusTokenId);
		switch (PlayerBonusTokenStatus.fromCode(entity.getStatus())) {
			case ACTIVE: {
				entity.setStatus(PlayerBonusTokenStatus.RESERVED.code());
				return playerBonusTokenMapper(playerBonusTokenRepository.save(entity));
			}
			case RESERVED: {
				return playerBonusTokenMapper(entity);
			}
		}
		throw new Status424InvalidBonusTokenStateException(PlayerBonusTokenStatus.fromCode(entity.getStatus()).label());
	}

	public PlayerBonusToken unreserveBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		lithium.service.casino.data.entities.PlayerBonusToken entity =
				handleBonusTokenValidationInternal(playerGuid, bonusTokenId);
		switch (PlayerBonusTokenStatus.fromCode(entity.getStatus())) {
			case RESERVED: {
				entity.setStatus(PlayerBonusTokenStatus.ACTIVE.code());
				return playerBonusTokenMapper(playerBonusTokenRepository.save(entity));
			}
			case ACTIVE: {
				return playerBonusTokenMapper(entity);
			}
		}
		throw new Status424InvalidBonusTokenStateException(PlayerBonusTokenStatus.fromCode(entity.getStatus()).label());
	}

	public PlayerBonusToken redeemBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		lithium.service.casino.data.entities.PlayerBonusToken entity =
				handleBonusTokenValidationInternal(playerGuid, bonusTokenId);

		return playerBonusTokenMapper(modifyStateIfNotFinal(entity, PlayerBonusTokenStatus.REDEEMED));

	}

	public PlayerBonusToken expireBonusToken(Long bonusTokenId
	) throws Status424InvalidBonusTokenStateException {

		lithium.service.casino.data.entities.PlayerBonusToken entity = playerBonusTokenRepository.findOne(bonusTokenId);

		return playerBonusTokenMapper(modifyStateIfNotFinal(entity, PlayerBonusTokenStatus.EXPIRED));
	}

	public Boolean cancelBonusTokenBoolean(String playerGuid, Long bonusTokenId){
		try {
			handleBonusTokenValidation(playerGuid, bonusTokenId);
			cancelBonusToken(bonusTokenId);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}
	public PlayerBonusToken cancelBonusToken(Long bonusTokenId
	) throws Status424InvalidBonusTokenStateException {

		lithium.service.casino.data.entities.PlayerBonusToken entity = playerBonusTokenRepository.findOne(bonusTokenId);

		return playerBonusTokenMapper(modifyStateIfNotFinal(entity, PlayerBonusTokenStatus.CANCELLED));
	}

	public BonusToken createBonusToken(final BonusRevision bonusRevision, final String currency,
									   final Double minimumOdds, final Long amountCents) {

		return bonusTokenRepository.save(BonusToken.builder()
				.bonusRevision(bonusRevision)
				.currency(currency)
				.minimumOdds(getMinimumOdds(minimumOdds, bonusRevision.getDomain().getName()))
				.amount(getEffectiveBonusTokenAmount(bonusRevision.getDomain().getName(), amountCents, null))
				.build());
	}

	public void createPlayerBonusTokens(PlayerBonusHistory playerBonusHistory) {
		BonusRevision bonusRevision = playerBonusHistory.getBonus();
		if (bonusRevision.getBonusTokens() == null || bonusRevision.getBonusTokens().isEmpty()) {
			return;
		}
		bonusRevision.getBonusTokens().stream().forEach(bonusToken -> {
			createPlayerBonusToken(
					bonusToken,
					userRepository
							.findOrCreateByGuid(playerBonusHistory.getPlayerBonus().getPlayerGuid(), User::new),
					playerBonusHistory.getCustomBonusTokenAmountCents(),
					playerBonusHistory.getId());
		});
	}

	public Page<lithium.service.casino.data.entities.PlayerBonusToken> findActiveBonusTokensForPlayer(
			String playerGuid, DataTableRequest dataTableRequest) {
		lithium.service.casino.data.entities.Domain domain = domainService.findOrCreate(StringUtils.substringBefore(playerGuid, "/"));

		return playerBonusTokenRepository.findByUserAndStatus(
				userRepository.findOrCreateByGuid(playerGuid, () -> User.builder().domain(domain).build()),
				PlayerBonusTokenStatus.ACTIVE.code(), dataTableRequest.getPageRequest());
	}
	public List<PlayerBonusToken> findActiveBonusTokensForPlayer(String playerGuid) {

		return playerBonusTokenRepository.findByUserAndStatus(
				userRepository.findOrCreateByGuid(playerGuid, User::new),
				PlayerBonusTokenStatus.ACTIVE.code())
			.stream()
			.map(pbt -> playerBonusTokenMapper(pbt))
			.collect(Collectors.toList());
	}

	public Page<lithium.service.casino.data.entities.PlayerBonusToken> findExpiredPlayerBonusTokens(Pageable pageRequest) {

		return playerBonusTokenRepository.findByExpiryDateBeforeAndStatus(
				DateTime.now().toDate(), PlayerBonusTokenStatus.ACTIVE.code(), pageRequest);
	}

	private lithium.service.casino.data.entities.PlayerBonusToken createPlayerBonusToken(
			final BonusToken bonusToken, final User user,
			final Long customTokenAmountCents, final Long playerBonusHistoryId) {

		//Basic init for player bonus token
		lithium.service.casino.data.entities.PlayerBonusToken playerBonusToken =
				lithium.service.casino.data.entities.PlayerBonusToken.builder()
						.bonusToken(bonusToken)
						.status(PlayerBonusTokenStatus.ACTIVE.code())
						.user(user)
						.createdDate(DateTime.now().toDate())
						.playerBonusHistoryId(playerBonusHistoryId)
						.expiryDate(getEffectiveExpiryDate(
								bonusToken.getBonusRevision().getValidDays(),
								bonusToken.getBonusRevision().getDomain().getName()))
						.build();

		//Use the custom token amount
		if (customTokenAmountCents != null && customTokenAmountCents > 0L) {
			playerBonusToken.setCustomTokenAmountCents(
					getEffectiveBonusTokenAmount(
							bonusToken.getBonusRevision().getDomain().getName(),
							customTokenAmountCents, bonusToken.getAmount()));
		}
		return playerBonusTokenRepository.save(playerBonusToken);
	}

	/**
	 * Used to alter a non-final bonus token state to the specified status
	 * @param entity
	 * @param status
	 * @return
	 * @throws Status424InvalidBonusTokenStateException
	 */
	private lithium.service.casino.data.entities.PlayerBonusToken modifyStateIfNotFinal(
			lithium.service.casino.data.entities.PlayerBonusToken entity,
			PlayerBonusTokenStatus status
	) throws
			Status424InvalidBonusTokenStateException {

		switch (PlayerBonusTokenStatus.fromCode(entity.getStatus())) {
			case ACTIVE:
			case RESERVED:
			{
				entity.setStatus(status.code());
				return playerBonusTokenRepository.save(entity);
			}
		}
		throw new Status424InvalidBonusTokenStateException(PlayerBonusTokenStatus.fromCode(entity.getStatus()).label());
	}

	/**
	 * Performs a bonus token integrity check on existence and player allocation
	 * @param playerGuid
	 * @param bonusTokenId
	 * @return
	 * @throws Status423InvalidBonusTokenException
	 */
	private lithium.service.casino.data.entities.PlayerBonusToken handleBonusTokenValidationInternal(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException {

		lithium.service.casino.data.entities.PlayerBonusToken token = playerBonusTokenRepository.findOne(bonusTokenId);
		String errorMessage = "";
		if (token != null) {
			if (token.getUser().getGuid().contentEquals(playerGuid)) {
				return token;
			}
			errorMessage = "No token " + bonusTokenId + " for player " + playerGuid;
		} else {
			errorMessage = "No token " + bonusTokenId + " exists";
		}
		throw new Status423InvalidBonusTokenException(errorMessage);
	}

	private static PlayerBonusToken playerBonusTokenMapper(lithium.service.casino.data.entities.PlayerBonusToken entity) {

		CurrencyAmount amount = CurrencyAmount.fromCents(entity.getBonusToken().getAmount());
		if (entity.getCustomTokenAmountCents() != null) {
			amount = CurrencyAmount.fromCents(entity.getCustomTokenAmountCents());
		}

		PlayerBonusToken playerBonusToken = PlayerBonusToken.builder()
				.bonusRevisionId(entity.getBonusToken().getBonusRevision().getId())
				.playerBonusHistoryId(entity.getPlayerBonusHistoryId())
				.amountDecimal(amount.toAmount().doubleValue())
				.createdDate(entity.getCreatedDate())
				.currency(entity.getBonusToken().getCurrency())
				.expiryDate(entity.getExpiryDate())
				.id(entity.getId())
				.minimumOdds(entity.getBonusToken().getMinimumOdds())
				.status(entity.getStatus())
				.build();

		return playerBonusToken;
	}

	public void deleteBonusTokensForRevision(BonusRevision bonusRevision) {
		bonusTokenRepository.deleteAll(bonusTokenRepository.findByBonusRevision(bonusRevision));
	}

	/**
	 * Determine the minimum odds to use. If provided, use the provided odds else use default domain odds.
	 * @param providedOdds
	 * @param domainName
	 * @return Minimum odds value
	 */
	private Double getMinimumOdds(final Double providedOdds, final String domainName) {
		AtomicReference<Double> effectiveMinimumOdds = new AtomicReference<>(0.0);
		if (providedOdds == null) {
			Domain domain = null;
			try {
				domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
			} catch (Status550ServiceDomainClientException e) {
				log.warn("Problem getting domain for default setting value lookup: " + e.getMessage());
				return effectiveMinimumOdds.get();
			}
			final Optional<String> maybeMinOdds = domain.findDomainSettingByName(BONUS_TOKEN_DEFAULT_MINIMUM_ODDS_NAME);
			maybeMinOdds.ifPresent(data -> {
				try {
					effectiveMinimumOdds.set(Double.parseDouble(data));
				} catch (NumberFormatException e) {
					log.warn("Default bonus token minimum odds has an error on domain " + domainName);
				}
			});
			return effectiveMinimumOdds.get();
		} else {
			return providedOdds;
		}
	}


	private Long getTokenValueBound(final String domainName, String boundName) {
		Long boundValueLimit = 0L;
		if (boundName.contentEquals(BONUS_TOKEN_DEFAULT_MAXIMUM_TOKEN_VALUE_NAME)) {
			boundValueLimit = Long.MAX_VALUE;
		}
		AtomicReference<Long> tokenBoundValue = new AtomicReference<>(boundValueLimit);

		Domain domain = null;
		try {
			domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		} catch (Status550ServiceDomainClientException e) {
			log.warn("Problem getting domain for default setting value lookup: " + e.getMessage());
			return tokenBoundValue.get();
		}
		final Optional<String> maybeBoundValue = domain.findDomainSettingByName(boundName);
		maybeBoundValue.ifPresent(data -> {
			try {
				tokenBoundValue.set(Long.parseLong(data));
			} catch (NumberFormatException e) {
				log.warn("Default bonus token boundary value has an error on domain " + domainName);
			}
		});
		return tokenBoundValue.get();
	}

	/**
	 * Modify the supplied bonus token value to fall in the provided default bounds for the domain if out of bounds
	 * @param domainName
	 * @param amount
	 * @Param fallback
	 * @return
	 */
	private Long getEffectiveBonusTokenAmount(final String domainName, final Long amount, final Long fallback) {
		Long minimumValue = getTokenValueBound(domainName, BONUS_TOKEN_DEFAULT_MINIMUM_TOKEN_VALUE_NAME);
		Long maximumValue = getTokenValueBound(domainName, BONUS_TOKEN_DEFAULT_MAXIMUM_TOKEN_VALUE_NAME);

		if (amount < minimumValue) {
			if (fallback != null) return fallback;
			log.warn("Default bonus token value is higher than the supplied value, using minimum: " + amount + " < " + minimumValue);
			return minimumValue;
		}

		if (amount > maximumValue) {
			if (fallback != null) return fallback;
			log.warn("Default bonus token value is lower than the supplied value, using maximum: " + amount + " > " + maximumValue);
			return maximumValue;
		}
		return amount;
	}

	/**
	 * Attempts to use the provided valid days if present. <br>
	 * If not present get the default bonus token expiration days from domain settings and produce a date or max date if none exists
	 * @param providedValidDays
	 * @param domainName
	 * @return
	 */
	private Date getEffectiveExpiryDate(final Integer providedValidDays, final String domainName) {
		AtomicReference<Integer> effectiveExpirationDays = new AtomicReference<>(365000); //About 1000 years
		if (providedValidDays == null) {
			Domain domain = null;
			try {
				domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
			} catch (Status550ServiceDomainClientException e) {
				log.warn("Problem getting domain for default setting value lookup: " + e.getMessage());
				return DateTime.now().plusDays(effectiveExpirationDays.get()).toDate();
			}
			final Optional<String> maybeExpiryDays = domain.findDomainSettingByName(BONUS_TOKEN_DEFAULT_EXPIRY_DURATION_DAYS_NAME);
			maybeExpiryDays.ifPresent(data -> {
				try {
					effectiveExpirationDays.set(Integer.parseInt(data));
				} catch (NumberFormatException e) {
					log.warn("Default bonus token expiration days has an error on domain " + domainName);
				}
			});
			return DateTime.now().plusDays(effectiveExpirationDays.get()).toDate();
		} else {
			return DateTime.now().plusDays(providedValidDays).toDate();
		}
	}
}
