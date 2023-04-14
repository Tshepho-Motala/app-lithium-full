package lithium.service.casino.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.objects.TranProcessResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.service.CasinoBalanceAdjustmentService;
import lithium.service.casino.service.CasinoBonusService;
import lithium.service.casino.service.CasinoService;
import lithium.service.casino.service.WinnerFeedService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status482PlayerBetPlacementNotAllowedException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
@RequiredArgsConstructor
// no tight coupling between client and service (so no implementation or dependence on service-client
public class BetController {

	private final ServiceCasinoConfigurationProperties serviceGamesConfig;
	private final CasinoService casinoService;
	private final CasinoBonusService casinoBonusService;
	private final WinnerFeedService winnerFeedService;
	private final LithiumServiceClientFactory services;
	private final CasinoBalanceAdjustmentService casinoBalanceAdjustmentService;
	private final UserApiInternalClientService userApiInternalClientService;
	private final CachingDomainClientService cachingDomainClientService;
	private final ProviderClientService providerClientService;
	private final LimitInternalSystemService limitInternalSystemService;
	private final LocaleContextProcessor localeContextProcessor;

	/**
	 * This method is used only to register a zero win transaction, should not be used for anything else.
	 * You have been warned, if you use this without knowing what you are doing, it will be a fuckup.
	 * Riaan knows when/where to use this. Riaan does not use this just anywhere. Be more like Riaan!
	 * @param request
	 * @return BetResponse
	 * @throws Exception
	 */
	@RequestMapping("/casino/zerowin")
	public BetResponse handleZeroWinRequest(@RequestBody BetRequest request) throws Exception {
		log.debug("ZeroWinRequest: "+request);

		if (request.getTranType() == null) {
			request.setTranType(CasinoTranType.CASINO_BET);
		}

		TranProcessResponse tranProcessResponse = TranProcessResponse.builder()
				.tranId(-1L)
				.build();
		
		String currency = (request.getCurrencyCode() != null && !request.getCurrencyCode().isEmpty())
				? request.getCurrencyCode()
				: casinoService.getCurrency(request.getDomainName());
		
		if (casinoService.getCustomerBalanceWithError(currency,request.getDomainName(), request.getUserGuid()) == null) {
			BetResponse btr = BetResponse.builder()
			.balanceCents(null) //call balance service
			.extSystemTransactionId(tranProcessResponse.getTranId()+"")
			.build();
			btr.setCode("Customer not found");
			return btr;
		}
		
		log.debug("No bonus processing, only processing win transaction.");
		tranProcessResponse = casinoService.processWin(
			0L,
			request.getDomainName(),
			request.getUserGuid(),
			request.getTransactionId(), 
			request.getProviderGuid(),
			request.getGameGuid(),
			request.getGameSessionId(),
			currency,
			request.getTranType(),
			request.getOriginalTransactionId(),
			request.getBonusTokenId(),
			request.getSessionId()
		);
		
		BetResponse btr = BetResponse.builder()
		.balanceCents(
			casinoService.getCustomerBalanceWithError(
					currency,
				request.getDomainName(),
				request.getUserGuid()
			)
		) //call balance service
		.extSystemTransactionId(tranProcessResponse.getTranId()+"") // call accounting service
		.build();
		
		if (tranProcessResponse.getTranId() <= 0) {
			btr.setCode("-1");
			btr.setResult("Problem performing transaction");
		} else {
			if (tranProcessResponse.isDuplicate()) {
				btr.setCode("DUPLICATE");
			}
		}
		return btr;
	}

	@RequestMapping("/casino/settle/v2")
	public Response<BetResponse> handleSettleRequestV2(
		@RequestBody BetRequest request,
		Locale locale
	) throws Status500UnhandledCasinoClientException {
		try {
			String[] domainAndPlayer = request.getUserGuid().split("/");
			localeContextProcessor.setLocaleContextHolder(locale.toLanguageTag(), domainAndPlayer[0]);
			return Response.<BetResponse>builder().data(handleBetRequestInternal(request, locale)).status(Status.OK).build();
		} catch (Exception ex) {
			log.error("Problem performing player limit checks for bet request: " + request, ex);
			throw new Status500UnhandledCasinoClientException(ex.getMessage());
		}
	}

	@RequestMapping("/casino/bet/v2")
	public Response<BetResponse> handleBetRequestV2(
		@RequestBody BetRequest request,
		Locale locale
	) throws
			Status405UserDisabledException,
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
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
			Status500UnhandledCasinoClientException,
			Status478TimeSlotLimitException,
			Status438PlayTimeLimitReachedException {
		//Perform restriction and limit check
		try {
			String[] domainAndPlayer = request.getUserGuid().split("/");
			localeContextProcessor.setLocaleContextHolder(locale.toLanguageTag(), domainAndPlayer[0]);
			String currency = (request.getCurrencyCode() != null && !request.getCurrencyCode().isEmpty())
				? request.getCurrencyCode()
				: casinoService.getCurrency(request.getDomainName());

			if (isBet(request.getTranType(), request.getBet())) {
				limitInternalSystemService.checkPlayerRestrictions(request.getUserGuid(), locale.toLanguageTag());
				limitInternalSystemService.checkLimits(request.getDomainName(), request.getUserGuid(), currency,
					request.getBet(), locale.toLanguageTag());
				limitInternalSystemService.checkPlayerBetPlacementAllowed(request.getUserGuid());
			}
			//Do original stuff and wrap
			return Response.<BetResponse>builder().data(handleBetRequestInternal(request, locale)).status(Status.OK).build();
		} catch (
			Status438PlayTimeLimitReachedException |
					Status478TimeSlotLimitException |
			Status405UserDisabledException |
			Status423InvalidBonusTokenException |
			Status424InvalidBonusTokenStateException |
			Status473DomainBettingDisabledException |
			Status474DomainProviderDisabledException |
			Status484WeeklyLossLimitReachedException  |
			Status485WeeklyWinLimitReachedException |
			Status490SoftSelfExclusionException |
			Status491PermanentSelfExclusionException |
			Status492DailyLossLimitReachedException |
			Status493MonthlyLossLimitReachedException |
			Status494DailyWinLimitReachedException |
			Status495MonthlyWinLimitReachedException |
			Status496PlayerCoolingOffException errorCodeException
		) {
			throw errorCodeException;
		} catch (Exception ex) {
			log.error("Problem performing player restriction and limit checks for bet request: " + request, ex);
			throw new Status500UnhandledCasinoClientException(ex.getMessage());
		}
	}
	
	@RequestMapping("/casino/bet")
	@Deprecated
	public BetResponse handleBetRequest(
			@RequestBody BetRequest request,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		String[] domainAndPlayer = request.getUserGuid().split("/");
		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);
		log.debug("BetRequest: " + request);
		return handleBetRequestInternal(request, null);
	}

	private BetResponse handleBetRequestInternal(
		@RequestBody BetRequest request,
		Locale locale
	) throws
			Status401UnAuthorisedException,
			Status405UserDisabledException,
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status473DomainBettingDisabledException,
			Status474DomainProviderDisabledException,
			Status500UserInternalSystemClientException,
			Status500UnhandledCasinoClientException,
			Status550ServiceDomainClientException,
			Status482PlayerBetPlacementNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status438PlayTimeLimitReachedException {
		boolean persistRound = false;
		if (request.getPersistRound() != null && request.getPersistRound()) {
			if (request.getBetTransactionId() == null || request.getBetTransactionId().trim().isEmpty()) {
				String msg = "\"persistRound\" is true but \"betTransactionId\" has not been provided. The"
						+ " adjustment cannot be completed.";
				log.error(msg + " | request: {}", request);
				throw new Status500UnhandledCasinoClientException(msg);
			}
			persistRound = true;
		}

		String localeStr = "en_US";
		if (locale != null) localeStr = locale.toLanguageTag();

		if (isBet(request.getTranType(), request.getBet())) {
			cachingDomainClientService.checkBettingEnabled(request.getDomainName(), localeStr);
			providerClientService.checkProviderEnabled(request.getDomainName(), request.getProviderGuid().split("/")[1], localeStr);
			userApiInternalClientService.performUserChecks(request.getUserGuid(), localeStr,
				request.getSessionId(), true, true, false);
			limitInternalSystemService.checkPlayerBetPlacementAllowed(request.getUserGuid());
		}

		//TODO when receiving a bet that equals a previous bet external ID, it does not adjust balance as expected, but it does affect playthrough.
		
		TranProcessResponse tranProcessResponse = TranProcessResponse.builder()
		.tranId(-1L)
		.build();
		
		// All logic down the line expect the values of bet, win and negative bet to be positive. They *sometimes* forget to 
		// Math.abs, and then we have funny mathematical issues. This ensures the contract is adhered to. Ie, don't fiddle... @Chris ;)
		if (request.getBet() != null && request.getBet() < 0) request.setBet(Math.abs(request.getBet()));
		if (request.getWin() != null && request.getWin() < 0) request.setWin(Math.abs(request.getWin()));
		if (request.getNegativeBet() != null && request.getNegativeBet() < 0) request.setNegativeBet(Math.abs(request.getNegativeBet()));

		if (request.getTranType() != null) {
			int tranTypes = 0;
			if (request.getBet() != null) tranTypes++;
			if (request.getWin() != null) tranTypes++;
			if (request.getNegativeBet() != null) tranTypes++;
			if (tranTypes != 1) {
				throw new java.lang.IllegalArgumentException("tranType may not be specified if bet and win is specified");
			}
		}

		String currency;
		try {
			currency = (request.getCurrencyCode() != null && !request.getCurrencyCode().isEmpty())
					? request.getCurrencyCode()
					: casinoService.getCurrency(request.getDomainName());
		} catch (Exception ex) {
			log.error("Problem retrieving currency: " + request, ex);
			throw new Status500UnhandledCasinoClientException(ex.getMessage());
		}

		Long balanceCents = casinoService.getCustomerBalanceWithError(
			currency,
			request.getDomainName(),
			request.getUserGuid()
		);

		if (balanceCents == null) {
			BetResponse btr = BetResponse.builder()
			.balanceCents(null) //call balance service
			.extSystemTransactionId(tranProcessResponse.getTranId()+"")
			.build();
			btr.setCode("Customer not found");
			return btr;
		}
		
		PlayerBonus currentBonus = null;
		
		if (request.getAlwaysReal() != null && request.getAlwaysReal()) {
			tranProcessResponse.setTranId(-2L);
		} else {
			currentBonus = casinoBonusService.findCurrentBonus(request.getUserGuid()); 
			if (currentBonus != null) {
				tranProcessResponse = casinoBonusService.process(request, currentBonus);
			} else {
				tranProcessResponse.setTranId(-2L);
			}
		}
		
		//Check if all balance adjustment values are zero
		boolean writeZeroValueTran = ((request.getBet() == null) || ((request.getBet() != null) && (request.getBet() == 0))) 
				&& ((request.getWin() == null) || ((request.getWin() != null) && (request.getWin() == 0)))
				&& ((request.getNegativeBet() == null) || ((request.getNegativeBet() != null) && (request.getNegativeBet() == 0)));


		boolean forceBet = false;
		boolean forceWin = false;
		if (request.getTranType() != null) {
			writeZeroValueTran = false;
			if (request.getTranType() == CasinoTranType.CASINO_BET) forceBet = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_BET) forceBet = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_FREE_BET) forceBet = true;
			if (request.getTranType() == CasinoTranType.CASINO_BET_FREESPIN) forceBet = true;
			if (request.getTranType() == CasinoTranType.CASINO_WIN) forceWin = true;
			if (request.getTranType() == CasinoTranType.CASINO_LOSS) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_WIN) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_LOSS) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_BET_VOID) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_FREE_WIN) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_FREE_LOSS) forceWin = true;
			if (request.getTranType() == CasinoTranType.VIRTUAL_FREE_BET_VOID) forceWin = true;
			if (request.getTranType() == CasinoTranType.SPORTS_BET) forceBet = true;
			if (request.getTranType() == CasinoTranType.SPORTS_FREE_BET) forceBet = true;
			if (request.getTranType() == CasinoTranType.SPORTS_WIN) forceWin = true;
			if (request.getTranType() == CasinoTranType.SPORTS_FREE_WIN) forceWin = true;
			if (request.getTranType() == CasinoTranType.SPORTS_LOSS) forceWin = true;
			if (request.getTranType() == CasinoTranType.SPORTS_FREE_LOSS) forceWin = true;
		}

		log.debug("writeZeroValueTran: "+writeZeroValueTran);
		if (tranProcessResponse.getTranId() == -2L) {
			boolean betFailed = false;
			log.info("No active bonus, normal bet processing.");
			if (((request.getBet() != null) && (request.getBet() != 0)) || writeZeroValueTran || forceBet) {
				if (balanceCents < request.getBet()) {
					// Sports transactions utilise a reserved fund, thus we are allowed to adjust into negative.
					// The reservation was used to check for available funds.
					if (!casinoService.isSportsTransaction(request.getTranType())) {
						BetResponse btr = BetResponse.builder()
								.balanceCents(balanceCents)
								.extSystemTransactionId("0")
								.build();
						btr.setErrorCode(Status471InsufficientFundsException.CODE);
						return btr;
					}
				}
				tranProcessResponse = casinoService.processBet(
					request.getBet() != null ? request.getBet() : 0L,
					request.getDomainName(),
					request.getUserGuid(),
					request.getTransactionId(), 
					request.getProviderGuid(),
					request.getGameGuid(),
					request.getGameSessionId(),
					currency,
					request.getTranType() == null ? CasinoTranType.CASINO_BET : request.getTranType(),
					request.getBonusTokenId(),
					request.getAdditionalReference(),
					request.getSessionId()
				);
				
				if (tranProcessResponse.getTranId() <= 0) betFailed = true;
			}
			
			//If bet failed, we don't process winnings on the transaction.
			if (!betFailed) {
				if (((request.getWin() != null) && (request.getWin() != 0)) | forceWin) {
					tranProcessResponse = casinoService.processWin(
						request.getWin(),
						request.getDomainName(),
						request.getUserGuid(),
						request.getTransactionId(), 
						request.getProviderGuid(),
						request.getGameGuid(),
						request.getGameSessionId(),
						currency,
						request.getTranType() == null ? CasinoTranType.CASINO_WIN : request.getTranType(),
						request.getOriginalTransactionId(),
						request.getBonusTokenId(),
						request.getSessionId()
					);
					winnerFeedService.addWinner(request);
				}
				if ((request.getNegativeBet() != null) && (request.getNegativeBet() != 0)) {
					//Tran ID might not be used(There will be a win tran id, sometimes, that is sent to remote provider)
					TranProcessResponse tmpTranProcessResponse = casinoService.processNegativeBet(
						request.getNegativeBet(),
						request.getDomainName(),
						request.getUserGuid(),
						request.getTransactionId(),
						request.getProviderGuid(),
						request.getGameGuid(),
						request.getGameSessionId(),
						currency,
						request.getSessionId()
					);
					
					if (tranProcessResponse.getTranId() == -2L) tranProcessResponse = tmpTranProcessResponse;
				}
			}
		}
		
		BetResponse btr = BetResponse.builder()
		.balanceCents(
			casinoService.getCustomerBalanceWithError(
				currency,
				request.getDomainName(),
				request.getUserGuid()
			)
		) //call balance service
		.extSystemTransactionId(tranProcessResponse.getTranId()+"") // call accounting service
		.build();
		
		if((request.getBet() != null) && (request.getBet() != 0) && (currentBonus != null)) {
			btr.setBonusBet(100L); //Percentage of bet that is a bonus
		}
		
		if((request.getWin() != null) && (request.getWin() != 0) && (currentBonus != null)) {
			btr.setBonusWin(100L); //Percentage of win that is a bonus
		}
		
		if((request.getNegativeBet() != null) && (request.getNegativeBet() != 0) && (currentBonus != null)) {
			btr.setBonusWin(100L); //Percentage of negative bet that is a bonus
		}
		
		if (tranProcessResponse.getTranId() <= 0) {
			btr.setCode("-1");
			btr.setResult("Problem performing transaction");
			// TODO we can definitely get more of a reason in here by extending the tranProcessResponse object with
			// an additional field and populating it in all the different error scenarios.
			btr.setErrorCode(Status500UnhandledCasinoClientException.CODE);
		} else {
			if (tranProcessResponse.isDuplicate()) {
				btr.setCode("DUPLICATE");
				btr.setErrorCode(Status409DuplicateSubmissionException.CODE);
			} else {
				if (persistRound) {
					casinoService.persistRound(request, tranProcessResponse.getTranId(), btr.getBalanceCents());
				}
			}
		}
		return btr;
	}

	private boolean isBet(CasinoTranType tranType, Long bet) {
		if (tranType != null) {
			return tranType.bet();
//			if (tranType.equals(CasinoTranType.CASINO_BET.)) return true;
//			if (tranType.equals(CasinoTranType.CASINO_BET_FREESPIN)) return true;
//			if (tranType.equals(CasinoTranType.VIRTUAL_BET)) return true;
//			if (tranType.equals(CasinoTranType.VIRTUAL_FREE_BET)) return true;
//			if (tranType.equals(CasinoTranType.SPORTS_BET)) return true;
//			if (tranType.equals(CasinoTranType.SPORTS_FREE_BET)) return true;
		}
		if (bet != null) return true;
		return false;
	}
}
