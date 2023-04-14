package lithium.service.casino.service;

import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.AdjustmentTransaction.AdjustmentResponseStatus;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.ServiceCasinoApplication;
import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.AccountInfoResponse;
import lithium.service.casino.data.enums.BetRequestKindEnum;
import lithium.service.casino.data.enums.BetResultRequestKindEnum;
import lithium.service.casino.data.objects.TranProcessResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.util.LabelManager;
import lithium.service.event.client.objects.EventStreamData;
import lithium.service.event.client.stream.EventStream;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserApiToken;
import lithium.service.user.client.service.LoginEventClientService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class CasinoService {


	@Setter @Autowired private LithiumServiceClientFactory services;
	@Setter @Autowired private CasinoGeoService casinoGeoService;
	@Setter @Autowired private EventStream eventStream;
	@Setter @Autowired private CurrencyService currencyService;
	@Setter @Autowired private BonusTokenService bonusTokenService;
	@Setter @Autowired private BetPersistService betPersistService;
	@Setter @Autowired private BetResultPersistService betResultPersistService;
	@Setter @Autowired private LoginEventClientService loginEventHelperService;

	public UserApiToken validateUserSession(String userGuid, String userApiToken) throws Exception {
		Response<UserApiToken> tokenResponse = getUserApiService().getApiTokenIfValid(userGuid, userApiToken);
		if(tokenResponse.getStatus() == Status.OK) {
			return tokenResponse.getData();
		}
		return null;
	}

	public AccountInfoResponse getUserInfo(String userGuid, String userApiToken) throws Exception {
		Response<lithium.service.user.client.objects.User> userResponse = getUserApiService().getUser(userGuid, userApiToken);

		if (userResponse.getStatus() == Status.OK) {
			AccountInfoResponse response = new AccountInfoResponse();
			String currency = currencyService.retrieveDomainFromDomainService(userResponse.getData().getDomain().getName()).getCurrency();
			response.setCode("0");
			response.setResult("OK");
			response.setCurrency(currency);
			response.setEmail(userResponse.getData().getEmail());
			response.setFirstName(userResponse.getData().getFirstName());
			response.setLastName(userResponse.getData().getLastName());
			response.setUserName(userGuid);
			response.setBalanceCents(getCustomerBalanceWithError(currency, userResponse.getData().getDomain().getName(), userGuid));

			return response;
		}

		return null;
	}

	public UserApiClient getUserApiService() throws Exception {
		UserApiClient cl = null;

		cl = services.target(UserApiClient.class,"service-user", true);

		return cl;
	}

	AccountingClient getAccountingService() {
		AccountingClient cl = null;
		try {
			cl = services.target(AccountingClient.class, "service-accounting", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return cl;
	}

	/**
	 * Retrieves all player account balances from the accounting service and consolidate them to get a unified balance for the customer.
	 * @param currency
	 * @param domainName
	 * @param userGuid
	 * @return unified net balance for the customer
	 */
	public Long getCustomerBalanceWithError(String currency, String domainName, String userGuid) {
		log.debug("getCustomerBalanceWithError("+currency+", "+domainName+", "+userGuid+")");
		long balance;

		try {
			Response<Map<String,Long>> balanceResponse = getAccountingService().getByAccountType(domainName, "PLAYER_BALANCE", currency, userGuid);
			//This indicates a new user since there is no record for the user in accounting
			//If there is some user creation delay we might return zero, when null would be more appropriate.
			if (balanceResponse.getStatus() == Status.NOT_FOUND) {
				log.warn("Customer balance could not be returned from accounting: " + balanceResponse.getMessage());
				return 0L;
			}

			//Some unknown problem happened, return null so we don't perform normal workflow processing
			if (balanceResponse.getStatus() != Status.OK) {
				throw new Exception(balanceResponse.getStatus() + " " + balanceResponse.getMessage());
			}

			balance = balanceResponse.getData().getOrDefault("PLAYER_BALANCE", 0L);

			balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L);

			balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS_PENDING", 0L);


		} catch (Exception e) {
			log.error("Could not get balance for user: " + userGuid, e);
			return null;
		}

		//Dispatch event if balance is zero
		if (balance <= 0L) {
			eventStream.register(EventStreamData.builder()
					.eventType(EventStreamData.EVENT_TYPE_ZERO_BALANCE)
					.ownerGuid(userGuid)
					.currencyCode(currency)
					.domainName(domainName)
					.build());
		}

		return balance;
	}

	public String accountCodeFromProviderGuid(String providerGuid) {
		int lastDash = providerGuid.lastIndexOf('-');
		return providerGuid.substring(lastDash+1).toUpperCase();
	}

	public boolean isSportsTransaction(CasinoTranType type) {
		if (type == CasinoTranType.SPORTS_BET) return true;
		if (type == CasinoTranType.SPORTS_FREE_BET) return true;
		if (type == CasinoTranType.SPORTS_WIN) return true;
		if (type == CasinoTranType.SPORTS_FREE_WIN) return true;
		if (type == CasinoTranType.SPORTS_LOSS) return true;
		return type == CasinoTranType.SPORTS_FREE_LOSS;
	}

	private void handleBonusTokenBet(String userGuid, Long bonusTokenId, LabelManager labelManager
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		PlayerBonusToken playerBonusToken = bonusTokenService.reserveBonusToken(userGuid, bonusTokenId);

		labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, playerBonusToken.getPlayerBonusHistoryId().toString());
		labelManager.addLabel(LabelManager.BONUS_REVISION_ID, playerBonusToken.getBonusRevisionId().toString());
		labelManager.addLabel(LabelManager.PLAYER_BONUS_TOKEN_ID, playerBonusToken.getId().toString());
	}

	public TranProcessResponse processBet(
			Long amountCents, String domainName, String userGuid, String tranId,
			String providerGuid, String gameGuid, String gameSessionId,
			String currencyCode, CasinoTranType type, Long bonusTokenId,
			String additionalReference, Long sessionId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		boolean allowNegativeAdjust = false;

		// Sports transactions utilise a reserved fund, thus we are allowed to adjust into negative.
		// The reservation was used to check for available funds.
		if (isSportsTransaction(type)) allowNegativeAdjust = true;
		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.ADDITIONAL_REFERENCE_ID, additionalReference)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);

		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}
		if (bonusTokenId != null) {
			handleBonusTokenBet(userGuid, bonusTokenId, labelManager);
		} //else {
//			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
//		}

		Response<AdjustmentTransaction> accTran;
		try {
			String currency = (currencyCode != null && !currencyCode.isEmpty())
							? currencyCode
							: currencyService.retrieveDomainFromDomainService(domainName).getCurrency();

			accTran = getAccountingService().adjust(
				(Math.abs(amountCents))*-1,
				new DateTime().toDateTimeISO().toString(),
				type.toString(), //transactionTypeCode
				type.toString() + "_" + accountCodeFromProviderGuid(providerGuid), //contraAccountCode
				type.toString(), //contraAccountTypeCode
				labelManager.getLabelArray(),
				currency,
				domainName,
				userGuid,
				User.SYSTEM_GUID,
				allowNegativeAdjust
			);
		} catch (Exception e) {
			//TODO we need to rethrow this...
			log.error("Could not process bet: userGuid " + userGuid + " amountCents " + amountCents + " tranId " + tranId + " gameGuid " + gameGuid, e);
			return TranProcessResponse.builder()
					.tranId(0L)
					.build();
		}

// This now gets handled by completed accounting rabbit events being processed in serviceXP
//		try {
//			if (accTran != null && accTran.getData() != null && accTran.getData().getAdjustmentResponse() == AdjustmentResponse.NEW) {
//				xpService.processXp(domainName, userGuid, accTran.getData().getTransactionId(), amountCents);
//			}
//		} catch (Exception e) {
//			log.error("Problem processing xp gain (" + accTran + ")" + e.getMessage(), e);
//		}

		try {
			if (accTran != null && accTran.getData() != null && accTran.getData().getStatus() == AdjustmentResponseStatus.NEW) {
				casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTran.getData().getTransactionId());
			}
		} catch (Exception e) {
			log.error("Problem adding geo device labels to transaction (" + accTran + ")" + e.getMessage(), e);
		}

		if (accTran != null && accTran.getData() != null && accTran.getStatus() == Status.OK &&
				accTran.getData().getStatus() != AdjustmentResponseStatus.ERROR) {
			return TranProcessResponse.builder()
					.tranId(accTran.getData().getTransactionId())
					.duplicate(accTran.getData().getStatus() == AdjustmentResponseStatus.DUPLICATE)
					.build();
		}

		log.error("Could not process bet: userGuid " + userGuid + " amountCents " + amountCents + " tranId " + tranId + " gameGuid " + gameGuid);
		//TODO why not throw here...
		return TranProcessResponse.builder()
				.tranId(0L)
				.build();
	}

	private void handleBonusTokenWin(String userGuid, Long bonusTokenId, CasinoTranType type, LabelManager labelManager
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		PlayerBonusToken playerBonusToken;

		switch (type) {
			case VIRTUAL_FREE_BET_VOID:
				playerBonusToken = bonusTokenService.unreserveBonusToken(userGuid, bonusTokenId);
				break;
			case VIRTUAL_FREE_LOSS:
			case VIRTUAL_FREE_WIN:
				playerBonusToken = bonusTokenService.redeemBonusToken(userGuid, bonusTokenId);
				break;
			default:
				log.warn("Bonus token id received for unsupported transaction type: " + type.value() + " bonus token: " + bonusTokenId);
				return;
		}

		labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, playerBonusToken.getPlayerBonusHistoryId().toString());
		labelManager.addLabel(LabelManager.BONUS_REVISION_ID, playerBonusToken.getBonusRevisionId().toString());
		labelManager.addLabel(LabelManager.PLAYER_BONUS_TOKEN_ID, playerBonusToken.getId().toString());
	}

	public TranProcessResponse processWin(Long amountCents, String domainName, String userGuid, String tranId,
					  String providerGuid, String gameGuid, String gameSessionId, String currencyCode,
					  CasinoTranType type, Long originalTransactionId, Long bonusTokenId, Long sessionId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {

		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);

		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}
		if (originalTransactionId != null) {
			labelManager.addLabel(LabelManager.ORIGINAL_TRANSACTION_ID, originalTransactionId.toString());
		}
		if (bonusTokenId != null) {
			handleBonusTokenWin(userGuid, bonusTokenId, type, labelManager);
		} //else {
//			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
//		}

		Response<AdjustmentTransaction> accTran;
		try {
			String currency = (currencyCode != null && !currencyCode.isEmpty())
					? currencyCode
					: currencyService.retrieveDomainFromDomainService(domainName).getCurrency();
			accTran = getAccountingService().adjust(
				(Math.abs(amountCents)),
				new DateTime().toDateTimeISO().toString(),
				type.toString(),
				type.toString() + "_" + accountCodeFromProviderGuid(providerGuid),
				type.toString(),
				labelManager.getLabelArray(),
				currency,
				domainName,
				userGuid,
				User.SYSTEM_GUID,
				false
			);
		} catch (Exception e) {
			log.error("Problem placing win", e);
			return TranProcessResponse.builder()
					.tranId(0L)
					.build();
		}

		try {
			if (accTran.getData().getStatus() == AdjustmentResponseStatus.NEW) {
				casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTran.getData().getTransactionId());
			}
		} catch (Exception e) {
			log.error("Problem adding geo device labels to transaction (" + accTran + ")" + e.getMessage(), e);
		}

		if (accTran != null && accTran.getStatus() == Status.OK
				&& accTran.getData().getStatus() != AdjustmentResponseStatus.ERROR) {
			return TranProcessResponse.builder()
					.tranId(accTran.getData().getTransactionId())
					.duplicate(accTran.getData().getStatus() == AdjustmentResponseStatus.DUPLICATE)
					.build();
		}

		log.error("Could not process win: userGuid " + userGuid + " amountCents " + amountCents + " tranId " + tranId + " gameGuid " + gameGuid);
		return TranProcessResponse.builder()
				.tranId(0L)
				.build();
	}

	public TranProcessResponse processNegativeBet(Long amountCents, String domainName, String userGuid, String tranId,
	        String providerGuid, String gameGuid, String gameSessionId, String currencyCode, Long sessionId) {
		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);
//				.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1")
//				.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}

		Response<AdjustmentTransaction> accTran;
		try {
			String currency = (currencyCode != null && !currencyCode.isEmpty())
					? currencyCode
					: currencyService.retrieveDomainFromDomainService(domainName).getCurrency();
			accTran = getAccountingService().adjust(
				Math.abs(amountCents),
				new DateTime().toDateTimeISO().toString(),
				CasinoTranType.CASINO_NEGATIVE_BET.toString(), // transactionTypeCode
				"CASINO_NEGATIVEBET_" + accountCodeFromProviderGuid(providerGuid), // contraAccountCode
				CasinoTranType.CASINO_NEGATIVE_BET.toString(), // contraAccountTypeCode
				labelManager.getLabelArray(),
				currency,
				domainName,
				userGuid,
				User.SYSTEM_GUID,
				false
			);
		} catch (Exception e) {
			log.error("Problem placing negative bet", e);
			return TranProcessResponse.builder()
					.tranId(0L)
					.build();
		}

		try {
			if (accTran.getData().getStatus() == AdjustmentResponseStatus.NEW) {
				casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTran.getData().getTransactionId());
			}
		} catch (Exception e) {
			log.error("Problem adding geo device labels to transaction (" + accTran + ")" + e.getMessage(), e);
		}

		if(accTran != null && accTran.getStatus() == Status.OK
				&& accTran.getData().getStatus() != AdjustmentResponseStatus.ERROR) {
			return TranProcessResponse.builder()
					.tranId(accTran.getData().getTransactionId())
					.duplicate(accTran.getData().getStatus() == AdjustmentResponseStatus.DUPLICATE)
					.build();
		}

		log.error("Could not process negative bet: userGuid " + userGuid + " amountCents " + amountCents + " tranId " + tranId + " gameGuid " + gameGuid);
		return TranProcessResponse.builder()
				.tranId(0L)
				.build();
	}

	public TranProcessResponse processReversal(String domainName, String userGuid, String originalRemoteTranId, String originalAccountCode, String originalAccountTypeCode, String reversalTransactionTypeCode, String currencyCode) {

		String logEntry = "processReversal domainName " + domainName + " userGuid " + userGuid + " originalRemoteTranId " + originalRemoteTranId
				+ " originalAccountCode " + originalAccountCode + " originalAccountTypeCode " + originalAccountTypeCode
				+ " reversalTransactionTypeCode " + reversalTransactionTypeCode + " currencyCode " + currencyCode;
		Response<AdjustmentTransaction> accTran = null;
		try {
			accTran = getAccountingService().rollback(
				new DateTime().toDateTimeISO().toString(),
				reversalTransactionTypeCode,
				ServiceCasinoApplication.TRAN_ID_REVERSE_LABEL,
				domainName, userGuid, User.SYSTEM_GUID, currencyCode,
				ServiceCasinoApplication.TRAN_ID_LABEL,
				originalRemoteTranId,
				originalAccountCode, originalAccountTypeCode);

		} catch (Exception e) {
			log.error("Problem requesting rolling back transaction " + logEntry, e);
		}

		if (accTran != null && accTran.getStatus() == Status.OK
				&& accTran.getData().getStatus() != AdjustmentResponseStatus.ERROR) {
			log.info("Rolled back transaction: " + accTran + " " + logEntry);
			return TranProcessResponse.builder()
					.tranId(accTran.getData().getTransactionId())
					.duplicate(accTran.getData().getStatus() == AdjustmentResponseStatus.DUPLICATE)
					.build();
		}

		// It is not an error if it did not find the transaction at this point, its only an error if we can't find it anywhere.
		return null;
	}

	public String getCurrency(String domainName) throws Exception {
		return currencyService.retrieveDomainFromDomainService(domainName).getCurrency();
	}

	public void persistRound(BetRequest request, Long lithiumAccountingId, Long balanceAfterCents) {
		persistRound(balanceAfterCents, request.getTranType(), request.getCurrencyCode(), request.getGameGuid(),
				request.getProviderGuid(), request.getRoundId(), request.getBetTransactionId(), request.getCheckSequence(),
				request.getSequenceNumber(), request.getBet(), request.getRoundFinished(), request.getReturns(),
				request.getTransactionTimestamp(), request.getUserGuid(), request.getDomainName(), lithiumAccountingId,
				request.getSessionId());
	}

	public void persistRound(Long balanceAfterCents, CasinoTranType casinoTranType, String currencyCode, String gameGuid,
	        String providerGuid, String roundId, String betTransactionId, boolean checkSequence, Integer sequenceNumber,
	        Long amountCents, boolean roundComplete, Double returns, long transactionTimestamp, String userGuid,
	        String domainName, Long lithiumAccountingId, Long sessionId) {
		Double balanceAfter = (balanceAfterCents != null)
							? CurrencyAmount.fromCents(balanceAfterCents).toAmount().doubleValue()
							: null;
		switch (casinoTranType) {
			case REWARD_BET:
			case CASINO_BET:
			case CASINO_BET_FREEGAME:
			case CASINO_BET_FREESPIN: {
				BetRequestKindEnum kind = BetRequestKindEnum.fromCasinoTranType(casinoTranType);
				try {
					betPersistService.persist(currencyCode, gameGuid, kind, providerGuid, roundId, betTransactionId,
							checkSequence, sequenceNumber, CurrencyAmount.fromCents(amountCents).toAmount().doubleValue(),
							transactionTimestamp, userGuid, domainName, lithiumAccountingId, balanceAfter, roundComplete, sessionId);
				} catch (Status409DuplicateSubmissionException | Status500UnhandledCasinoClientException e) {
					// TODO: This would have been caught already by the bet processing above.
					//       Can possibly remove.
				}
				break;
			}
			case CASINO_BET_ROLLBACK:
			case REWARD_BET_ROLLBACK:
			case CASINO_WIN:
			case REWARD_WIN:
			case CASINO_LOSS:
			case REWARD_LOSS:
			case CASINO_VOID:
			case CASINO_WIN_FREEGAME:
			case CASINO_WIN_FREESPIN:
			case CASINO_LOSS_FREESPIN:
			case CASINO_LOSS_FREEGAME:
			case CASINO_WIN_JACKPOT:
			case CASINO_WIN_FREESPIN_JACKPOT:{
				try {
					BetResultRequestKindEnum kind = BetResultRequestKindEnum.fromCasinoTranType(casinoTranType);
					betResultPersistService.persist(userGuid, gameGuid, domainName, providerGuid, roundId, currencyCode, kind,
							checkSequence, sequenceNumber, betTransactionId, roundComplete, returns, transactionTimestamp,
							lithiumAccountingId);
				} catch (Status409DuplicateSubmissionException | Status500UnhandledCasinoClientException e) {
					// TODO: Duplicate submission would have been caught already by the bet processing above.
					// FIXME:Bet round probably needs to be checked before processing the win.
					// N.b: re fixme above, bet round is now written if not found. Will re-evaluate and remove fixme
					//		when work on https://playsafe.atlassian.net/browse/LC-243 is completed.
				}
				break;
			}
			default: log.warn("Unhandled bet round persist requested. [casinoTranType="+casinoTranType+", userGuid="+userGuid
					+", betTransactionId="+betTransactionId+", roundId="+roundId+"]");
		}
	}
}
