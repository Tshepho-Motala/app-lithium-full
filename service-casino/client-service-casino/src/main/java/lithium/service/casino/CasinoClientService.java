package lithium.service.casino;

import java.util.Optional;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.client.BetResultClient;
import lithium.service.casino.client.BonusTokenClient;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.MultiBetClient;
import lithium.service.casino.client.SystemBetClient;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.client.objects.response.LastBetResultResponse;
import lithium.service.casino.exceptions.Status411InvalidUserGuidException;
import lithium.service.casino.exceptions.Status412InvalidCustomFreeMoneyAmountException;
import lithium.service.casino.exceptions.Status413NoValidBonusFoundForCodeException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status444UniqueConstraintViolationException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status475NullVariablesException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@SuppressWarnings("deprecation")
@Service
@Slf4j
public class CasinoClientService implements CasinoTransactionDetailClient, BonusTokenClient, MultiBetClient {
	@Autowired @Setter CachingDomainClientService cachingDomainClientService;
	@Autowired @Setter LithiumServiceClientFactory services;

	private CasinoClient getCasinoService() throws Status500UnhandledCasinoClientException {
		try {
			return services.target(CasinoClient.class,"service-casino", true);
		} catch (Exception e) {
			log.error("Problem getting casino service", e);
			throw new Status500UnhandledCasinoClientException("Unable to retrieve casino client proxy: " + e.getMessage());
		}
	}

	private SystemBetClient getSystemBetService() throws Status500UnhandledCasinoClientException {
		try {
			return services.target(SystemBetClient.class,"service-casino", true);
		} catch (Exception e) {
			log.error("Problem getting system bet service", e);
			throw new Status500UnhandledCasinoClientException("Unable to retrieve system bet client proxy: " + e.getMessage());
		}
	}

	private BonusTokenClient getBonusTokenService() throws Status500UnhandledCasinoClientException {
		try {
			return services.target(BonusTokenClient.class,"service-casino", true);
		} catch (Exception e) {
			log.error("Problem getting bonus token service", e);
			throw new Status500UnhandledCasinoClientException("Unable to retrieve casino client proxy: " + e.getMessage());
		}
	}

	private BetResultClient getSystemBetResultService() throws Status500UnhandledCasinoClientException {
		try {
			return services.target(BetResultClient.class,"service-casino", true);
		} catch (Exception e) {
			log.error("Problem getting system bet result service", e);
			throw new Status500UnhandledCasinoClientException("Unable to retrieve casino client proxy: " + e.getMessage());
		}
	}

	public BalanceResponse getPlayerBalance(String domain, String playerGuid, String currencyCode) throws Status500UnhandledCasinoClientException {
		BalanceResponse balanceResponse = null;
		try {
			BalanceRequest br = new BalanceRequest();
			br.setDomainName(domain);
			br.setUserGuid(playerGuid);
			br.setCurrencyCode(currencyCode);
			balanceResponse = getCasinoService().handleBalanceRequest(br);
		} catch (Exception e) {
			log.error("Unable to get player balance. Player: " + playerGuid + " | Response: " + balanceResponse, e);
			throw new Status500UnhandledCasinoClientException("Unable to get player balance.");
		}
		return balanceResponse;
	}

	// https://gitlab.com/search?utf8=%E2%9C%93&snippets=&scope=&repository_ref=&search=.handleBetRequest%28&group_id=4885878&project_id=15160900
	// https://gitlab.com/search?utf8=%E2%9C%93&snippets=&scope=&repository_ref=&search=getExtSystemTransactionId&group_id=4885878&project_id=15160900

	public BetResponse handleSettle(
			String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
			String transactionId, String playerGuid, String gameId, String gameSessionId,
			Long originalTransactionId, Long bonusTokenId, Long sessionId
	) throws Status500UnhandledCasinoClientException {
		return handleSettle(domainName, currencyCode, type, provider, amount, transactionId, playerGuid, gameId,
				gameSessionId, originalTransactionId, bonusTokenId, null, null, null,
				null, null, null, null, null, sessionId);
	}

	public BetResponse handleSettle(
		String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
		String transactionId, String playerGuid, String gameId, String gameSessionId,
		Long originalTransactionId, Long bonusTokenId, Boolean persistRound, String betTransactionId,
		String roundId, Boolean roundComplete, Boolean checkSequence, Integer sequenceNumber, Double returns,
		Long transactionTimestamp, Long sessionId
	) throws Status500UnhandledCasinoClientException {
		BetRequest request = handleInternalPrepareRequest(
			domainName,
			currencyCode,
			type,
			provider,
			amount,
			transactionId,
			playerGuid,
			gameId,
			gameSessionId,
			originalTransactionId,
			bonusTokenId,
			null,
			persistRound,
			betTransactionId,
			roundId,
			roundComplete,
			checkSequence,
			sequenceNumber,
			returns,
			transactionTimestamp,
			sessionId
		);

		BetResponse betResponse;
		Response<BetResponse> response = null;
		try {
			//TODO: Figure out locale
			if (!type.bet()) {
				response = getCasinoService().handleSettleRequestV2(request, null);
			} else {
				throw new Status500UnhandledCasinoClientException("Wrong type for request.");
			}

			if (response.isSuccessful()) {
				betResponse = response.getData();
			} else {
				log.error("Unable to handle bet request [request=" + request + ", type=" + type + ", response=" + response + "]");
				throw new Status500UnhandledCasinoClientException(response.getMessage());
			}
			log.debug("Response from casino [response=" + response + "]");
		} catch (Status500UnhandledCasinoClientException errorCodeException) {
			throw errorCodeException;
		} catch (Exception e) {
			log.error("Unable to handle bet request [request="+request+", type="+type+", response="+response+"]", e);
			throw new Status500UnhandledCasinoClientException("Transaction handle failed: "+ExceptionMessageUtil.allMessages(e));
		}
		return betResponse;
	}


	public BetResponse handleBet(
			String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
			String transactionId, String playerGuid, String gameId, String gameSessionId,
			Long originalTransactionId, Long bonusTokenId, Long sessionId
	) throws
			Status405UserDisabledException,
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status438PlayTimeLimitReachedException,
			Status471InsufficientFundsException,
			Status473DomainBettingDisabledException,
			Status474DomainProviderDisabledException,
			Status478TimeSlotLimitException,
			Status484WeeklyLossLimitReachedException,
			Status485WeeklyWinLimitReachedException,
			Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException,
			Status492DailyLossLimitReachedException,
			Status493MonthlyLossLimitReachedException,
			Status494DailyWinLimitReachedException,
			Status495MonthlyWinLimitReachedException,
			Status496PlayerCoolingOffException,
			Status500UnhandledCasinoClientException
	{
		return handleBet(
				domainName, currencyCode, type, provider, amount,
				transactionId, playerGuid, gameId, gameSessionId,
				originalTransactionId, bonusTokenId, null, null, null,
				null, null, null, null, sessionId
		);
	}

	public BetResponse handleBet(
		String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
		String transactionId, String playerGuid, String gameId, String gameSessionId,
		Long originalTransactionId, Long bonusTokenId, String additionalReference, Boolean persistRound,
		String betTransactionId, String roundId, Boolean checkSequence, Integer sequenceNumber, Long transactionTimestamp,
		Long sessionId
	) throws
		Status405UserDisabledException,
		Status423InvalidBonusTokenException,
		Status424InvalidBonusTokenStateException,
		Status438PlayTimeLimitReachedException,
		Status471InsufficientFundsException,
		Status473DomainBettingDisabledException,
		Status474DomainProviderDisabledException,
		Status478TimeSlotLimitException,
		Status484WeeklyLossLimitReachedException,
		Status485WeeklyWinLimitReachedException,
		Status490SoftSelfExclusionException,
		Status491PermanentSelfExclusionException,
		Status492DailyLossLimitReachedException,
		Status493MonthlyLossLimitReachedException,
		Status494DailyWinLimitReachedException,
		Status495MonthlyWinLimitReachedException,
		Status496PlayerCoolingOffException,
		Status500UnhandledCasinoClientException
	{
		BetRequest request = handleInternalPrepareRequest(
			domainName,
			currencyCode,
			type,
			provider,
			amount,
			transactionId,
			playerGuid,
			gameId,
			gameSessionId,
			originalTransactionId,
			bonusTokenId,
			additionalReference,
			persistRound,
			betTransactionId,
			roundId,
			false,
			checkSequence,
			sequenceNumber,
			null,
			transactionTimestamp,
			sessionId
		);

		BetResponse betResponse;
		Response<BetResponse> response = null;
		try {
			//TODO: Figure out locale
			if (type.bet()) {
				response = getCasinoService().handleBetRequestV2(request, null);
			} else {
				throw new Status500UnhandledCasinoClientException("Wrong type for request.");
			}

			if (response.isSuccessful()) {
				betResponse = response.getData();
			} else {
				log.error("Unable to handle bet request [request=" + request + ", type=" + type + ", response=" + response + "]");
				throw new Status500UnhandledCasinoClientException(response.getMessage());
			}
			log.debug("Response from casino [response=" + response + "]");
		} catch (
			Status405UserDisabledException |
			Status423InvalidBonusTokenException |
			Status424InvalidBonusTokenStateException |
			Status438PlayTimeLimitReachedException |
			Status473DomainBettingDisabledException |
			Status474DomainProviderDisabledException |
			Status478TimeSlotLimitException |
			Status484WeeklyLossLimitReachedException |
			Status485WeeklyWinLimitReachedException |
			Status490SoftSelfExclusionException |
			Status491PermanentSelfExclusionException |
			Status492DailyLossLimitReachedException |
			Status493MonthlyLossLimitReachedException |
			Status494DailyWinLimitReachedException |
			Status495MonthlyWinLimitReachedException |
			Status496PlayerCoolingOffException |
			Status500UnhandledCasinoClientException errorCodeException
		) {
			throw errorCodeException;
		} catch (Exception e) {
			log.error("Unable to handle bet request [request="+request+", type="+type+", response="+response+"]", e);
			throw new Status500UnhandledCasinoClientException("Transaction handle failed: "+ExceptionMessageUtil.allMessages(e));
		}

		// TODO There is no clear API here for insufficient funds or other errors
		//      currently this one is thrown even if its a resubmission of a duplicate tran.
		if (Long.parseLong(betResponse.getExtSystemTransactionId()) <= 0) {
			throw new Status471InsufficientFundsException();
		}

		return betResponse;
	}


	private BetRequest handleInternalPrepareRequest(
			String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
			String transactionId, String playerGuid, String gameId, String gameSessionId,
			Long originalTransactionId, Long bonusTokenId, Long sessionId)
			throws Status500UnhandledCasinoClientException {
		return handleInternalPrepareRequest(domainName, currencyCode, type, provider, amount, transactionId, playerGuid,
				gameId, gameSessionId, originalTransactionId, bonusTokenId, null, null,
				null, null, null, null, null, null,
				null, sessionId);
	}

	private BetRequest handleInternalPrepareRequest(
			String domainName, String currencyCode, CasinoTranType type, String provider, Long amount,
			String transactionId, String playerGuid, String gameId, String gameSessionId,
			Long originalTransactionId, Long bonusTokenId, String additionalReference, Boolean persistRound,
			String betTransactionId, String roundId, Boolean roundFinished, Boolean checkSequence, Integer sequenceNumber,
			Double returns, Long transactionTimestamp, Long sessionId) throws Status500UnhandledCasinoClientException {
		BetRequest request = BetRequest.builder().build();
		request.setCurrencyCode(currencyCode);
		request.setTranType(type);
		request.setOriginalTransactionId(originalTransactionId);

		switch (type) {
			case CASINO_BET:
			case CASINO_BET_FREESPIN:
			case VIRTUAL_BET:
			case VIRTUAL_FREE_BET:
			case SPORTS_BET:
			case SPORTS_FREE_BET:
				request.setBet(amount);
				break;
			case CASINO_WIN:
			case CASINO_WIN_JACKPOT:
			case CASINO_VOID:
			case CASINO_WIN_FREESPIN:
			case CASINO_WIN_FREESPIN_JACKPOT:
			case CASINO_LOSS:
			case CASINO_LOSS_FREESPIN:
			case VIRTUAL_WIN:
			case VIRTUAL_LOSS:
			case VIRTUAL_BET_VOID:
			case VIRTUAL_FREE_WIN:
			case VIRTUAL_FREE_LOSS:
			case VIRTUAL_FREE_BET_VOID:
			case SPORTS_WIN:
			case SPORTS_FREE_WIN:
			case SPORTS_LOSS:
			case SPORTS_FREE_LOSS:
				request.setWin(amount);
				break;
			default:
				throw new Status500UnhandledCasinoClientException("Invalid tran type for handle: " + type);
		}

		try {
			request.setAlwaysReal(alwaysReal(domainName, currencyCode));
		} catch (Status550ServiceDomainClientException e) {
			throw new Status500UnhandledCasinoClientException(e.getMessage());
		}
		request.setTransactionId(transactionId);
		request.setDomainName(domainName);
		request.setProviderGuid(domainName + "/" + provider);
		request.setUserGuid(playerGuid);
		request.setGameGuid(domainName + "/" + provider + "_" + gameId);
		request.setGameSessionId(gameSessionId);
		request.setBonusTokenId(bonusTokenId);
		request.setAdditionalReference(additionalReference);
		request.setPersistRound(persistRound);
		request.setBetTransactionId(betTransactionId);
		request.setRoundId(roundId);
		request.setRoundFinished(roundFinished);
		request.setCheckSequence(checkSequence);
		request.setSequenceNumber(sequenceNumber);
		request.setReturns(returns);
		request.setTransactionTimestamp(transactionTimestamp);
		request.setSessionId(sessionId);
		log.debug("Request to casino [request="+request+"]");

		return request;
	}

	private boolean alwaysReal(String domainName, String currencyCode) throws Status550ServiceDomainClientException {
		String domainDefault = cachingDomainClientService.retrieveDomainFromDomainService(domainName).getCurrency();
		return !domainDefault.equalsIgnoreCase(currencyCode);
	}

	@Override
	public List<TransactionDetailPayload> findTransactionDetailUrls(
		List<TransactionDetailPayload> transactionDetailRequestList
	) throws
		Status422InvalidParameterProvidedException,
		Status500UnhandledCasinoClientException,
		Status511UpstreamServiceUnavailableException,
		Status512ProviderNotConfiguredException
	{
		CasinoTransactionDetailClient casinoTransactionDetailClient =
				CasinoTransactionDetailClient.lookupService(CasinoTransactionDetailClient.class,"service-casino", services);
		return casinoTransactionDetailClient.findTransactionDetailUrls(transactionDetailRequestList);
	}

	@Override
	public TransactionDetailPayload findTransactionDetailUrl(
		TransactionDetailPayload transactionDetailRequest
	) throws
		Status422InvalidParameterProvidedException,
		Status500UnhandledCasinoClientException,
		Status511UpstreamServiceUnavailableException,
		Status512ProviderNotConfiguredException
	{
		CasinoTransactionDetailClient casinoTransactionDetailClient =
				CasinoTransactionDetailClient.lookupService(CasinoTransactionDetailClient.class,"service-casino", services);
		return casinoTransactionDetailClient.findTransactionDetailUrl(transactionDetailRequest);
	}

	@Override
	public List<PlayerBonusToken> findBonusTokensByPlayer(String playerGuid
	) throws
			Status500UnhandledCasinoClientException {
		return getBonusTokenService().findBonusTokensByPlayer(playerGuid);
	}

	@Override
	public PlayerBonusToken validateBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status500UnhandledCasinoClientException {
		return getBonusTokenService().validateBonusToken(playerGuid, bonusTokenId);
	}

	@Override
	public PlayerBonusToken reserveBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException {
		return getBonusTokenService().reserveBonusToken(playerGuid, bonusTokenId);
	}

	@Override
	public PlayerBonusToken unreserveBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException {
		return getBonusTokenService().unreserveBonusToken(playerGuid, bonusTokenId);
	}

	@Override
	public PlayerBonusToken redeemBonusToken(String playerGuid, Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException {
		return getBonusTokenService().redeemBonusToken(playerGuid, bonusTokenId);
	}

	@Override
	public BalanceAdjustmentResponse multiBetV1(
		BalanceAdjustmentRequest request,
		String locale
	) throws
		Status401UnAuthorisedException,
		Status405UserDisabledException,
		Status471InsufficientFundsException,
		Status473DomainBettingDisabledException,
		Status474DomainProviderDisabledException,
		Status484WeeklyLossLimitReachedException,
		Status485WeeklyWinLimitReachedException,
		Status490SoftSelfExclusionException,
		Status491PermanentSelfExclusionException,
		Status492DailyLossLimitReachedException,
		Status493MonthlyLossLimitReachedException,
		Status494DailyWinLimitReachedException,
		Status495MonthlyWinLimitReachedException,
		Status496PlayerCoolingOffException,
		Status511UpstreamServiceUnavailableException,
		Status550ServiceDomainClientException,
		Status478TimeSlotLimitException,
			Status438PlayTimeLimitReachedException
	{
		MultiBetClient multiBetClient = CasinoTransactionDetailClient.lookupService(MultiBetClient.class, "service-casino", services);
		return multiBetClient.multiBetV1(request, locale);
	}


	@Override
	public BalanceAdjustmentResponse negativeBalanceAdjust(
		BalanceAdjustmentRequest request,
		String locale
	) throws
		Status511UpstreamServiceUnavailableException
	{
		MultiBetClient multiBetClient = CasinoTransactionDetailClient.lookupService(MultiBetClient.class, "service-casino", services);
		return multiBetClient.negativeBalanceAdjust(request, locale);
	}

	public void completeBetRound(String domainName,
								 String providerGuid,
								 String roundId)
			throws Status474BetRoundNotFoundException, Status500UnhandledCasinoClientException {
		getSystemBetService().completeBetRound(domainName, providerGuid, roundId);
	}

	public void completeBetRound(String domainName,
								 String providerGuid,
								 String roundId,
								 Optional <String> gameGuid,
								 Optional <String> userGuid) throws Status475NullVariablesException, Status500UnhandledCasinoClientException {
		getSystemBetService().completeBetRound(domainName, providerGuid, roundId, gameGuid, userGuid);
	}

	public LastBetResultResponse findLastBetResult(String domainName, String providerGuid, String roundId) throws Status474BetRoundNotFoundException, Status500UnhandledCasinoClientException {
		return getSystemBetResultService().findLastBetResult(domainName, providerGuid, roundId);
	}
}
