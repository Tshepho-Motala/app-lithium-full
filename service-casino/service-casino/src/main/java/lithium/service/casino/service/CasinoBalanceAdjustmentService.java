package lithium.service.casino.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.BalanceAdjustmentResponseComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.EBalanceAdjustmentResponseStatus;
import lithium.service.casino.data.entities.BonusRoundTrack;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.casino.data.repositories.PlayerBonusPendingRepository;
import lithium.service.casino.service.util.AdjustmentRequestFactory;
import lithium.service.casino.service.util.EActiveBonusWorkflowStatus;
import lithium.service.casino.service.util.EPendingBonusWorkflowStatus;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.reward.client.PlayerRewardUpdateClientService;
import lithium.service.user.client.service.LoginEventClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

@Service
@Slf4j
public class CasinoBalanceAdjustmentService {
	
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private AdjustmentRequestFactory adjustmentRequestFactory;
	@Autowired private BonusRoundTrackService bonusRoundTrackService;
	@Autowired private CasinoService casinoService;
	@Autowired private CasinoBonusService casinoBonusService;
	@Autowired private CasinoBonusFreespinService casinoBonusFreespinService;
	@Autowired private PlayerBonusPendingRepository playerBonusPendingRepository;
	@Autowired private CasinoBonusTransactionService casinoBonusTransactionService; 
	@Autowired private WinnerFeedService winnerFeedService;
	@Autowired private CasinoGeoService casinoGeoService;
	@Autowired private AccountingClientService accountingClientService;
	@Autowired private LoginEventClientService loginEventHelperService;

	@Autowired private PlayerRewardUpdateClientService playerRewardUpdateClientService;

	/**
	 * Negative balance adjustments as per :
	 * LIVESCORE-1112 - PLAT-166 Handling Of Negative Balance
	 * https://playsafe.atlassian.net/browse/LIVESCORE-1112
	 *
	 * Business:
	 * As a rule of thumb - an account balance can never be negative.
	 * Sometimes we get re-settlements of bets which need to take money away from an account,
	 * and if the player has already spent it, then his account might go into negative.
	 * The account might also go into negative in cases there are odds that are something like 1-2.34534543645
	 * and the system did not round up the amount properly and the balance is left with -0.000003432567.
	 *
	 * We need the system to adjust these cases to 0, and mark those adjustments for reporting purposes.
	 *
	 * @param request
	 * @param locale
	 * @return Response<BalanceAdjustmentResponse>
	 */
	@TimeThisMethod
	public Response<BalanceAdjustmentResponse> processNegativeBalanceAdjustment(BalanceAdjustmentRequest request, Locale locale) {
		Map<String,Long> customerBalanceMap = null;
		try {
			SW.start("getCustomerBalanceMap");
			customerBalanceMap = getCustomerBalanceMap(request);
			SW.stop();

			validateRequest(request);

			// Populate labels for this transaction
			SW.start("addBasicAdjustmentLabels");
			LabelManager labelManager = new LabelManager();
			addBasicAdjustmentLabels(request, labelManager);
			SW.stop();

			//Perform actual financial adjustments
			SW.start("adjustPlayerBalance");
			AdjustmentResponse adjustmentResponse = adjustPlayerBalance(request, locale, customerBalanceMap, labelManager, null);
			SW.stop();

			if (!request.getRealMoneyOnly()) casinoBonusService.registerUserEventPlayerBonusPostTransactionDisplay(request.getDomainName(), request.getUserGuid());

			SW.start("addTransactionGeoDeviceLabels");
			addTransactionGeoDeviceLabels(request, adjustmentResponse.getAdjustments());
			SW.stop();

			return constructSuccessWithBalance(request, adjustmentResponse.getAdjustments(), labelManager);

		} catch (Exception ex) {
			log.error("Problem performing negative adjustment transaction: " + request, ex);
			return constructErrorWithBalance(EBalanceAdjustmentResponseStatus.INTERNAL_ERROR, customerBalanceMap);
		}
	}

	/**
	 * Wrapper for performing a casino balance adjustment transactions.
	 * All logic is contained in this call and bonus transaction management also forms part of this if a bonus is present.
	 * @param request
	 * @param locale
	 * @return Response<BalanceAdjustmentResponse>
	 */
	@TimeThisMethod
	public Response<BalanceAdjustmentResponse> processBalanceAdjustment(BalanceAdjustmentRequest request, Locale locale) {
		Map<String,Long> customerBalanceMap = null;
		try {
			SW.start("getCustomerBalanceMap");
			customerBalanceMap = getCustomerBalanceMap(request);
			SW.stop();

			validateRequest(request);

			//Check for sufficient funds
			SW.start("hasSufficientFunds");
			if (!hasSufficientFunds(request, locale, customerBalanceMap)) {
				log.debug("Insufficient funds for: (" + request + ") and balances: " + customerBalanceMap.toString());
				SW.stop();
				return constructErrorWithBalance(EBalanceAdjustmentResponseStatus.INSUFFICIENT_FUNDS, customerBalanceMap);
			}
			SW.stop();
			
			// Populate labels for this transaction
			SW.start("addBasicAdjustmentLabels");
			LabelManager labelManager = new LabelManager();
			addBasicAdjustmentLabels(request, labelManager);
			SW.stop();

			//Removed as per request from architects due to bug fix being required for LSPLAT-505
			//SW.start("performRealMoneyEscrowIfRequired");
			//performRealMoneyEscrowIfRequired(request, locale, customerBalanceMap, labelManager);
			//SW.stop();
			
			// Find player bonus or make an empty one if one does not exist.
			SW.start("playerBonus");
			PlayerBonus playerBonus = findOrCreateEmptyPlayerBonus(request.getUserGuid());
			if (!request.getRealMoneyOnly()) addBonusAdjustmentLabels(playerBonus, labelManager);
			SW.stop();
			
			//Perform actual financial adjustments
			SW.start("adjustPlayerBalance");
			AdjustmentResponse adjustmentResponse = adjustPlayerBalance(request, locale, customerBalanceMap, labelManager, playerBonus);
			SW.stop();

			SW.start("performBonusRelatedUpdates");
			performBonusRelatedUpdates(request, playerBonus, adjustmentResponse.getAdjustments(), locale, labelManager);
			SW.stop();

			winnerFeedService.addWinner(request);

			if (!request.getRealMoneyOnly()) casinoBonusService.registerUserEventPlayerBonusPostTransactionDisplay(request.getDomainName(), request.getUserGuid());

			SW.start("addTransactionGeoDeviceLabels");
			addTransactionGeoDeviceLabels(request, adjustmentResponse.getAdjustments());
			SW.stop();
			
			return constructSuccessWithBalance(request, adjustmentResponse.getAdjustments(), labelManager);

		} catch (Status414AccountingTransactionDataValidationException | Status415NegativeBalanceException e) {
			log.error("Problem performing adjustment transaction: " + request, e);
			EBalanceAdjustmentResponseStatus status = EBalanceAdjustmentResponseStatus.fromCode(e.getCode());
			return constructErrorWithBalance(status, customerBalanceMap);
		} catch (Exception ex) {
			log.error("Problem performing adjustment transaction: " + request, ex);
			return constructErrorWithBalance(EBalanceAdjustmentResponseStatus.INTERNAL_ERROR, customerBalanceMap);
		}
	}

	private void validateRequest(BalanceAdjustmentRequest request) throws Status500InternalServerErrorException {
		if (request.getPersistRound() != null && request.getPersistRound()) {
			for (BalanceAdjustmentComponent component : request.getAdjustmentComponentList()) {
				if (component.getBetTransactionId() == null || component.getBetTransactionId().trim().isEmpty()) {
					String msg = "\"persistRound\" is true but \"betTransactionId\" has not been provided. The"
							+ " adjustment cannot be completed.";
					log.error(msg + " | request: {}", request);
					throw new Status500InternalServerErrorException(msg);
				}
			}
		}
	}

	private void addTransactionGeoDeviceLabels(BalanceAdjustmentRequest request, ArrayList<AdjustmentTransaction> adjustmentResponseList) {
		adjustmentResponseList.forEach(adjTran -> {
			if (adjTran.isNew()) {
				try {
					casinoGeoService.addTransactionGeoDeviceLabels(request.getUserGuid(), adjTran.getTransactionId());
				} catch (Exception e) {
					// Nothing to do in this case too bad if we can't add geo labels
				}
			}
		});
	}
	
	private Response<BalanceAdjustmentResponse> constructErrorWithBalance(final EBalanceAdjustmentResponseStatus adjustmentStatus, final Map<String, Long> customerBalanceMap) {
		return Response.<BalanceAdjustmentResponse>builder()
		.status(Status.OK)
		.data(BalanceAdjustmentResponse.builder()
				.result(adjustmentStatus)
				.balanceCents(getTotalBalance(customerBalanceMap))
				.build())
		.build();
	}
	
	/**
	 * Produces a populated adjustment response object for a successful adjustment transaction.
	 * @param request
	 * @param adjustmentTransactionList
	 * @param labelManager
	 * @return Response<BalanceAdjustmentResponse>
	 */
	private Response<BalanceAdjustmentResponse> constructSuccessWithBalance(final BalanceAdjustmentRequest request, final ArrayList<AdjustmentTransaction> adjustmentTransactionList, final LabelManager labelManager) {
		
		final Map<String, Long> customerBalanceMap =  getCustomerBalanceMap(request);
		final String bonusHistoryId = labelManager.findValueForLabelKey(LabelManager.PLAYER_BONUS_HISTORY_ID);
		
		Long bonusPercentage = 0L;
		if (bonusHistoryId != null && !bonusHistoryId.contentEquals("-1")) {
			bonusPercentage = 100L;
		}
		
		ArrayList<BalanceAdjustmentResponseComponent> adjustmentResponseComponentList = new ArrayList<>();
		adjustmentTransactionList.forEach(adjTran -> {
			//TODO: Decide on whether the individual tran response should be an actual new enum type.
			BalanceAdjustmentResponseComponent respComp = new BalanceAdjustmentResponseComponent(adjTran.getStatus().toString(), ""+adjTran.getTransactionId());
			adjustmentResponseComponentList.add(respComp);
		});
		
		return Response.<BalanceAdjustmentResponse>builder()
		.status(Status.OK)
		.data(BalanceAdjustmentResponse.builder()
				.result(EBalanceAdjustmentResponseStatus.SUCCESS)
				.balanceCents(getTotalBalance(customerBalanceMap))
				.playerBonusHistoryId(bonusHistoryId)
				.bonusBetPercentage(bonusPercentage)
				.bonusWinPercentage(bonusPercentage)
				.adjustmentResponseComponentList(adjustmentResponseComponentList)
				.build())
		.build();
	}

	/**
	 * Retrieve PlayerBonus or an a newly created player bonus if none exists.
	 * @param userGuid
	 * @return PlayerBonus
	 */
	private PlayerBonus findOrCreateEmptyPlayerBonus(final String userGuid) {
		PlayerBonus playerBonus = casinoBonusService.findCurrentBonus(userGuid);
		if (playerBonus == null) {
			playerBonus = casinoBonusService.findOrCreatePlayerBonus(userGuid);
			playerBonus.setBalance(0L); // avoid null issue. Should maybe just add it to builder default
		}
		return playerBonus;
	}
	
	private void performBonusRelatedUpdates(BalanceAdjustmentRequest request, PlayerBonus playerBonus, ArrayList<AdjustmentTransaction> adjustmentTransactionList, Locale locale, LabelManager labelManager) {
		if (request.getRealMoneyOnly()) return;

		boolean notNewTrans = false;
		long newTranCount = adjustmentTransactionList
			.stream()
			.filter(adjTran -> {
				return adjTran.isNew();
			})
			.count();
		if (newTranCount != adjustmentTransactionList.size()) {
			return;
		}
		
		//FIXME: Make sure these are not duplicate trans. We don't want to duplicates to cause playthrough and freespins etc
		updateFreespinCounter(request, playerBonus.getCurrent());
		
		bonusRoundTrackService.createOrUpdateBonusRound(request, playerBonus.getCurrent());
		
		savePlayThrough(request, playerBonus);
		
		if (request.getRoundFinished()) {
			try {
				EPendingBonusWorkflowStatus bonusActivationOutcome = performBonusActivationLoop(playerBonus, true);
				Map<String,Long> customerBalanceMapPostUpdate = getCustomerBalanceMap(request);
				// Release escrow if we have no more pending bonuses left or we have a positive bonus balance
				if (bonusActivationOutcome == EPendingBonusWorkflowStatus.NO_PENDING_BONUS_LEFT ||
					customerBalanceMapPostUpdate.getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L) >= 0L) {
					performRealMoneyEscrowCompletionIfRequired(request, locale, labelManager);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// This is an unfinished round, nothing needs to happen here for now
		}
	}
	private AdjustmentResponse adjustPlayerBalance(
			BalanceAdjustmentRequest request, Locale locale,
			Map<String, Long> customerBalanceMap, LabelManager labelManager,
			PlayerBonus playerBonus
	) throws
			Status414AccountingTransactionDataValidationException,
			Status415NegativeBalanceException,
			Status500InternalServerErrorException,
			Status510AccountingProviderUnavailableException,
			Status550ServiceDomainClientException {

		// Determine if all is well with the account to allow transactions
		boolean adjustmentAllowed = isAdjustmentAllowed(request, playerBonus, customerBalanceMap);
		//TODO: Decide if the notification side-effect should live on the adjustment allowed method or should it be handled by the parent method?
		if (!adjustmentAllowed) return null;

		//ArrayList<AdjustmentRequestComponent> adjustmentRequestList = new ArrayList<>();
		AdjustmentRequest adjustmentRequest = AdjustmentRequest.builder()
				.domainName(request.getDomainName())
				.build();
		//TODO: Might need to add some logic here and add transaction components in for freerounds to get the desired transactional consistency effect
		// On the other hand, the provider implementations should actually take care of that by creating zero bet and win when required

		for (BalanceAdjustmentComponent component : request.getAdjustmentComponentList()) {
			LabelManager labelManagerCopy = LabelManager.instance().addLabelArray(labelManager.getLabelArray());
			if (component.getLabelValues()!=null) labelManagerCopy.addLabelArray(component.getLabelValues());
			if (component.getTransactionIdLabelOverride() != null && !component.getTransactionIdLabelOverride().trim().isEmpty()) {
				labelManagerCopy.addLabel(LabelManager.TRANSACTION_ID, component.getTransactionIdLabelOverride());
			}
			if (component.getAdditionalReference() != null) {
				labelManagerCopy.addLabel(LabelManager.ADDITIONAL_REFERENCE_ID, component.getAdditionalReference());
			}

			if (request.getPlayerRewardTypeHistoryId() != null) {
				labelManagerCopy.addLabel(LabelManager.PLAYER_REWARD_TYPE_HISTORY_ID, request.getPlayerRewardTypeHistoryId().toString());
			}
			if (request.getPlayerBonusHistoryId() != null) {
				labelManagerCopy.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, request.getPlayerBonusHistoryId().toString());
			}

			//TODO VERIFY both label values related to CASINO_BET_ROLLBACK and CASINO_WIN_ROLLBACK
			if (component.getReversalBetTransactionId() != null) {
				labelManagerCopy.addLabel(LabelManager.REVERSE_TRANSACTION_ID, component.getReversalBetTransactionId());
				labelManagerCopy.addLabel(LabelManager.ORIGINAL_TRANSACTION_ID, component.getReversalBetTransactionId());
			}
			if (request.getPlayerRewardTypeHistoryId() != null) {
				log.warn("GOING TO SVC-REWARD!!!");
				try {
					if (component.getAdjustmentType() == EBalanceAdjustmentComponentType.REWARD_BET) {
						playerRewardUpdateClientService.updatePlayerRewardCounter(request.getPlayerRewardTypeHistoryId());
					}
					//TODO: subtract for reversal?
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
      AdjustmentRequestComponent adjustmentRequestComponent = adjustmentRequestFactory.constructAdjustmentRequestComponent(request, component, labelManagerCopy, playerBonus);
			if (adjustmentRequestComponent == null) adjustmentRequestComponent = adjustmentRequestFactory.constructRewardAdjustmentRequestComponent(request, component, labelManagerCopy);
			if (adjustmentRequestComponent != null) adjustmentRequest.add(adjustmentRequestComponent);
		}

		try {
			AdjustmentResponse adjustmentResponse = accountingClientService.adjust(adjustmentRequest);
			if (request.getPersistRound() != null && request.getPersistRound()) {
				persistRound(adjustmentResponse, request);
			}
			return adjustmentResponse;
		} catch (
				Status414AccountingTransactionDataValidationException |
						Status415NegativeBalanceException |
						Status500InternalServerErrorException |
						Status510AccountingProviderUnavailableException e) {
			log.error("Problem performing adjustment response: " + e.getMessage() + " Req: " + adjustmentRequest, e);
			throw e;
		}
	}

	private void persistRound(AdjustmentResponse adjustmentResponse, BalanceAdjustmentRequest request) {
		for (int i = 0; i < adjustmentResponse.getAdjustments().size(); i++) {
			BalanceAdjustmentComponent component = request.getAdjustmentComponentList().get(i);
			AdjustmentTransaction adjustmentTransaction = adjustmentResponse.getAdjustments().get(i);

			if (adjustmentTransaction.isNew()) {
				CasinoTranType casinoTranType = null;
				switch (component.getAdjustmentType()) {
					case CASINO_BET:
						casinoTranType = CasinoTranType.CASINO_BET;
						break;
					case CASINO_BET_REVERSAL:
						casinoTranType = CasinoTranType.CASINO_BET_ROLLBACK;
						break;
					case CASINO_FREEROUND_BET:
						casinoTranType = CasinoTranType.CASINO_BET_FREESPIN;
						break;
					case CASINO_BET_FREEGAME:
						casinoTranType = CasinoTranType.CASINO_BET_FREEGAME;
						break;
					case CASINO_WIN:
						casinoTranType = CasinoTranType.CASINO_WIN;
						break;
					case CASINO_WIN_FREEGAME:
						casinoTranType = CasinoTranType.CASINO_WIN_FREEGAME;
						break;
					case CASINO_FREEROUND_WIN:
					case CASINO_FREEROUND_REAL_MONEY_WIN:
						casinoTranType = CasinoTranType.CASINO_WIN_FREESPIN;
						break;
					case CASINO_WIN_JACKPOT:
						casinoTranType = CasinoTranType.CASINO_WIN_JACKPOT;
						break;
					case CASINO_FREEROUND_WIN_JACKPOT:
						casinoTranType = CasinoTranType.CASINO_WIN_FREESPIN_JACKPOT;
						break;
					case CASINO_LOSS:
						casinoTranType = CasinoTranType.CASINO_LOSS;
						break;
					case CASINO_FREEROUND_LOSS:
						casinoTranType = CasinoTranType.CASINO_LOSS_FREESPIN;
						break;
					case CASINO_LOSS_FREEGAME:
						casinoTranType = CasinoTranType.CASINO_LOSS_FREEGAME;
						break;
					case CASINO_VOID:
						casinoTranType = CasinoTranType.CASINO_VOID;
						break;
					//TODO: this should not be here ... Riaan
					case REWARD_BET:
						casinoTranType = CasinoTranType.REWARD_BET;
						break;
					case REWARD_BET_REVERSAL:
						casinoTranType = CasinoTranType.REWARD_BET_ROLLBACK;
						break;
					case REWARD_WIN:
						casinoTranType = CasinoTranType.REWARD_WIN;
						break;
					case REWARD_WIN_REVERSAL:
						casinoTranType = CasinoTranType.REWARD_WIN_ROLLBACK;
						break;
					case REWARD_LOSS:
						casinoTranType = CasinoTranType.REWARD_LOSS;
						break;
					default: log.warn("Unhandled bet round persist requested. [AdjustmentType="+component.getAdjustmentType()+", userGuid="+request.getUserGuid()
							+", betTransactionId="+request.getTransactionId()+", roundId="+request.getRoundId()+"]");
				}
				if (casinoTranType != null) {
					Long amountCents = ((casinoTranType.bet()) ||
							(casinoTranType.compareTo(CasinoTranType.CASINO_BET_ROLLBACK) == 0) ||
							(casinoTranType.compareTo(CasinoTranType.REWARD_BET_ROLLBACK) == 0))
									 ? component.getAmountAbs()
									 : null;
					Double returns = null;

					switch (casinoTranType) {
						case CASINO_WIN:
						case REWARD_WIN:
						case CASINO_WIN_FREESPIN:
						case CASINO_WIN_FREEGAME:
						case CASINO_WIN_JACKPOT:
						case CASINO_WIN_FREESPIN_JACKPOT:
							returns = CurrencyAmount.fromCents(component.getAmountAbs()).toAmount().doubleValue();
							break;
						case CASINO_VOID:
						case CASINO_LOSS:
						case REWARD_LOSS:
						case CASINO_LOSS_FREESPIN:
						case CASINO_LOSS_FREEGAME:
						case CASINO_BET_ROLLBACK:
						case REWARD_BET_ROLLBACK:
							returns = 0.0;
							break;
					}
					// FIXME: No balanceAfterCents
					casinoService.persistRound(null, casinoTranType, request.getCurrencyCode(),
							request.getGameGuid(), request.getProviderGuid(), request.getRoundId(),
							component.getBetTransactionId(), false, null, amountCents,
							request.getRoundFinished(), returns, new Date().getTime(), request.getUserGuid(),
							request.getDomainName(), adjustmentTransaction.getTransactionId(), request.getSessionId());
				}
			}
		}
	}

//	/**
//	 * Runs in a recursive loop performing pending bonus activations and completions/cancellation if conditions are met.
//	 * @param playerBonus
//	 * @throws Exception
//	 */
//	private void performBonusActivationLoop(PlayerBonus playerBonus) throws Exception {
//		EPendingBonusWorkflowStatus pendingActivationStatus = activateNextPendingBonus(playerBonus);
//		if (pendingActivationStatus == EPendingBonusWorkflowStatus.ACTIVATED_PENDING_BONUS && isBonusAllowedToEnd(playerBonus)) {
//			EActiveBonusWorkflowStatus activeBonusStatus = completeOrCancelBonusIfPossible(playerBonus);
//			if (activeBonusStatus == EActiveBonusWorkflowStatus.COMPLETED_BONUS || 
//				activeBonusStatus == EActiveBonusWorkflowStatus.CANCELLED_BONUS) {
//				performBonusActivationLoop(playerBonus);
//			}
//		}
//	}
	
	/**
	 * Perform a recursive pending bonus activation loop with an initial completion or cancellation.
	 * This method is doing checks to see if the bonus is allowed to end.
	 * 
	 * @param playerBonus
	 * @param isFirst
	 * @return Outcome of the last pending bonus activationS.
	 * @throws Exception
	 */
	private EPendingBonusWorkflowStatus performBonusActivationLoop(PlayerBonus playerBonus, boolean isFirst) throws Exception {
		EPendingBonusWorkflowStatus pendingActivationStatus = EPendingBonusWorkflowStatus.NO_PENDING_ACTIVATION_PERFORMED;
		
		if (!isFirst) {
			pendingActivationStatus = activateNextPendingBonus(playerBonus);
		}
		
		if ((pendingActivationStatus == EPendingBonusWorkflowStatus.ACTIVATED_PENDING_BONUS || isFirst)
			&& isBonusAllowedToEnd(playerBonus)) {
			EActiveBonusWorkflowStatus activeBonusStatus = completeOrCancelBonusIfPossible(playerBonus);
			if (activeBonusStatus == EActiveBonusWorkflowStatus.COMPLETED_BONUS || 
				activeBonusStatus == EActiveBonusWorkflowStatus.CANCELLED_BONUS) {
				pendingActivationStatus = performBonusActivationLoop(playerBonus, false);
			}
		}
		return pendingActivationStatus;
	}
	
	/**
	 * Completes or cancels the current bonus based on player bonus balance.
	 * Checks for outstanding freespins and incomplete rounds should have been done before calling this.
	 * @see #isBonusAllowedToEnd(PlayerBonus)
	 * @param playerBonus
	 * @return
	 * @throws Exception
	 */
	private EActiveBonusWorkflowStatus completeOrCancelBonusIfPossible(PlayerBonus playerBonus) throws Exception {
		if (playerBonus.getCurrent() == null) return EActiveBonusWorkflowStatus.NO_BONUS;
		
		long playThrough = playerBonus.getCurrent().getPlayThroughCents();
		long playThroughRequired = playerBonus.getCurrent().getPlayThroughRequiredCents();
		casinoBonusService.getCasinoBonusBalance(playerBonus); //This has side-effect (populates the playerBonus with latest balance)
		
		// Check to make sure we don't complete a bonus with a negative balance. This needs to cancel the bonus.
		// By the time we get here all checks for outstanding freespins and incomplete rounds should have been done.
		if (playerBonus.getBalance() < 0L) {
			casinoBonusTransactionService.cancelBonus(playerBonus);
			return EActiveBonusWorkflowStatus.CANCELLED_BONUS;
		}
		
		if (playThrough >= playThroughRequired) {
			//wager requirements met, lets finish bonus, and do balance adjustments.
			log.info("playThrough:"+playThrough+" playThroughRequired:"+playThroughRequired);
			casinoBonusTransactionService.completeBonus(playerBonus);
			return EActiveBonusWorkflowStatus.COMPLETED_BONUS;
		}
		return EActiveBonusWorkflowStatus.ACTIVE_BONUS;
	}
	
	/**
	 * Iterates over the adjustment components and adjusts the play through accordingly.
	 * @param request
	 * @param playerBonus
	 */
	private void savePlayThrough(BalanceAdjustmentRequest request, PlayerBonus playerBonus) {
		if (playerBonus.getCurrent() == null) return;
		
		request.getAdjustmentComponentList().forEach(component -> {
			try {
				casinoBonusService.savePlayThrough(
						playerBonus.getCurrent(), 
						component.getAccountEffectAmount(), 
						casinoBonusService.getGameRulePercentage(request.getDomainName(), request.getGameGuid(), playerBonus.getCurrent()));
			} catch (Exception e) {
				// TODO Add error handling here
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Perform a check of free round bet type and subtracts a freespin if required.
	 * Currently a hack is implemented to check if it is a feature spin using round tracking.
	 * This might not be 100% accurate but it is a step up from just tracking all free spins as non-feature spins.
	 * @param request
	 * @param playerBonusHistory
	 */
	private void updateFreespinCounter(final BalanceAdjustmentRequest request, final PlayerBonusHistory playerBonusHistory) {
		for (BalanceAdjustmentComponent adjustmentComponent : request.getAdjustmentComponentList()) {
			//TODO: Make sure the provider implementations now start providing zero bets for freespins
			if (adjustmentComponent.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET) {
				//Make sure this is not a feature spin
				if (request.getRoundFinished() == true) {
					BonusRoundTrack bonusRound = bonusRoundTrackService.findBonusRound(request);
					if (bonusRound != null) {
						// This is a feature round, since we already have a round id in the db. 
						// Feature spins happen with the same round id.
					} else {
						// This is a freespin, not a feature spin
						casinoBonusFreespinService.subtractFreespin(request.getUserGuid(), request.getBonusId());
					}
				} else {
					// This is a feature round, since freespins are always round finished type.
				}
			}
		}
	}
	
	/**
	 * Check for validity of an adjustment request.
	 * If the current bonus balance is in a negative and the 
	 * adjustment is not part of an incomplete round or free rounds, the adjustment request is rejected.
	 * 
	 * A notification will also be sent  to the user if this is configured.
	 * @param request
	 * @param playerBonus
	 * @param customerBalanceMap
	 * @return Adjustment validity outcome
	 */
	private boolean isAdjustmentAllowed(final BalanceAdjustmentRequest request, final PlayerBonus playerBonus, final Map<String, Long> customerBalanceMap) {
		if (request.getRealMoneyOnly()) return true;

		List<BonusRoundTrack> outstandingRounds = bonusRoundTrackService.getUnfinishedRoundsOnBonus(playerBonus.getCurrent());
		
		if (!outstandingRounds.isEmpty() && customerBalanceMap.getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L) < 0L) {
			if (!request.getRoundFinished()) {
				boolean partOfActiveRound = outstandingRounds
					.stream()
					.filter(outstanding -> { return outstanding.getRoundId().contentEquals(request.getRoundId()); })
					.findFirst()
					.isPresent();
				
				if (!partOfActiveRound) {
					// Check if it is a free round transaction, they will be allowed
					boolean isFreeround = request.getAdjustmentComponentList().stream().filter(component -> {
						return (component.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET ||
							component.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN ||
							component.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_FEATURE_BET ||
							component.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_FEATURE_WIN);
					})
					.findFirst()
					.isPresent();
					
					// The transaction will not be allowed since it is not a free round or part of an unfinished round
					if (!isFreeround) {
						//TODO: Notify user that bet could not be processed due to outstanding active rounds and a negative bonus balance
						return false;
					}
				}
			}
		}
		return true;
	}

	private void addBasicAdjustmentLabels(BalanceAdjustmentRequest request, LabelManager labelManager) {
		labelManager.addLabel(LabelManager.TRANSACTION_ID, request.getTransactionId());
		labelManager.addLabel(LabelManager.PROVIDER_GUID, request.getProviderGuid());
		labelManager.addLabel(LabelManager.GAME_GUID, request.getGameGuid());
//		labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//		labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
		if (request.getSessionId() != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(request.getSessionId()));
		}
		if (request.getRoundId() != null && !request.getRoundId().trim().isEmpty()) {
			labelManager.addLabel(LabelManager.ROUND_ID, request.getRoundId());
		}
		if (request.getTransactionTiebackId() != null) {
			labelManager.addLabel(LabelManager.TRANSACTION_TIEBACK_ID, request.getTransactionTiebackId());
		}
		if (request.getExternalTimestamp() != null) {
			labelManager.addLabel(LabelManager.EXTERNAL_TIMESTAMP, String.valueOf(request.getExternalTimestamp()));
		}
	}
	
	private void addBonusAdjustmentLabels(PlayerBonus playerBonus, LabelManager labelManager) {
		if (playerBonus.getCurrent() != null) {
			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, playerBonus.getCurrent().getId().toString());
			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, playerBonus.getCurrent().getBonus().getId().toString());
		}
	}

	/**
	 * Performs a sanity check on available funds for a player versus the adjustment transaction requirement.
	 * @param request
	 * @param locale
	 * @param customerBalanceMap
	 * @return true if there is sufficient funds. False if there is insufficient funds.
	 */
	private boolean hasSufficientFunds(final BalanceAdjustmentRequest request, final Locale locale, final Map<String,Long> customerBalanceMap) {
		if (request.getAllowNegativeBalanceAdjustment() != null && request.getAllowNegativeBalanceAdjustment()) return true;

		Long totalDebit = request.getTotalDebitAmountCents();
		
		Long totalCustomerBalance = 0L;
		for (Entry<String, Long> balance: customerBalanceMap.entrySet()) {
			// Hotfix for LSPLAT-778 - PLAT-1491 - LSNOC-105 - Negative balance in customer account

			//Comps Engine has account balances for free spins, and we need to subtract from those balances when a player places a free spin bet
			// e.g. for roxor it will be PLAYER_BALANCE_REWARD_RX_FREESPIN
			boolean playerHasRewardBalanceAccount = balance.getKey().toUpperCase().startsWith("PLAYER_BALANCE_REWARD");
			if (!balance.getKey().contentEquals("PLAYER_BALANCE") &&
					!balance.getKey().contentEquals("PLAYER_BALANCE_CASINO_BONUS") && !playerHasRewardBalanceAccount) {
				continue;
			}
			totalCustomerBalance += balance.getValue();
		}
		
		log.debug("Sufficient fund check for balance adjust (" + request + ") : required: " + totalDebit +" available: " + totalCustomerBalance);
		
		return (totalDebit > totalCustomerBalance) ? false : true;
	}
	
	private void performRealMoneyEscrowIfRequired(final BalanceAdjustmentRequest request, final Locale locale, final Map<String, Long> customerBalanceMap, final LabelManager labelManager) throws Exception {
		if (request.getAllowNegativeBalanceAdjustment()) return;

		Long amountToTakeFromEscrow = request.getTotalDebitAmountCents() - getTotalBonusBalance(customerBalanceMap);
		
		if (amountToTakeFromEscrow > 0L) {
			ArrayList<AdjustmentRequestComponent> adjustmentList = new ArrayList<>();
			adjustmentList.add(adjustmentRequestFactory.createTransferToEscrowRequestComponent(
					request, 
					new BalanceAdjustmentComponent(
							EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_REAL_TO_ESCROW, 
							amountToTakeFromEscrow, 
							null, null),
					labelManager));
			
			Response<ArrayList<AdjustmentTransaction>> adjustmentResponse = getAccountingService().adjustMultiBatch(adjustmentList);
			if (adjustmentResponse.getStatus() != Status.OK) {
				log.error("Error performing escrow transaction for balance adjustment request: " + request + " adjustment response: " + adjustmentResponse);
				throw new Exception("Error in performing escrow transaction");
			}
		}
	}
	
	/**
	 * Remove money from escrow account. 
	 * Zero out the bonus balance and transferring the remainder to real balance.
	 * @param request
	 * @param locale
	 * @param labelManager
	 * @throws Exception
	 */
	private void performRealMoneyEscrowCompletionIfRequired(final BalanceAdjustmentRequest request, final Locale locale, final LabelManager labelManager) throws Exception {

		final Map<String, Long> customerBalanceMap = getCustomerBalanceMap(request);
		Long activeBonusBalance = customerBalanceMap.getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L);
		Long escrowBalance = customerBalanceMap.getOrDefault("PLAYER_BALANCE_CASINO_ESCROW", 0L);
		Long excessToTransferToReal  = 0L;
		ArrayList<AdjustmentRequestComponent> adjustmentList = new ArrayList<>();
		
		// Check if bonus balance is still in a negative and adjust it to zero from escrow account.
		if (activeBonusBalance < 0L) {
			excessToTransferToReal = escrowBalance + activeBonusBalance;
			if (excessToTransferToReal < 0L) {
				log.error("We have encountered a situation where the escrow is not enough to cover the bonus balance deficit. " + request + " balanceMap: " + customerBalanceMap);
				//FIXME: Decide what to do in this case. Perhaps try to get the outstanding amount from real. If not possible send notification to admin staff.
			}
			
			adjustmentList.add(adjustmentRequestFactory.createTransferFromEscrowToBonusRequestComponent(
					request, 
					new BalanceAdjustmentComponent(
							EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_ESCROW_TO_BONUS, 
							activeBonusBalance, 
							null, null),
					labelManager));
		}
		
		// If there is still funds in the escrow account, transfer them back to the real balance account.
		if (excessToTransferToReal > 0L) {
			adjustmentList.add(adjustmentRequestFactory.createTransferToEscrowRequestComponent(
					request, 
					new BalanceAdjustmentComponent(
							EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_ESCROW_TO_REAL, 
							excessToTransferToReal, 
							null, null),
					labelManager));
		}
		
		Response<ArrayList<AdjustmentTransaction>> adjustmentResponse = getAccountingService().adjustMultiBatch(adjustmentList);
		if (adjustmentResponse.getStatus() != Status.OK) {
			log.error("Error performing escrow completion transaction for balance adjustment request: " + request + " adjustment response: " + adjustmentResponse);
			throw new Exception("Error in performing escrow transaction");
		}
	}

	/**
	 * Convenience method to get total available active bonus and pending bonus balance as a single unit
	 * @param customerBalanceMap
	 * @return Total player bonus balance
	 */
	private Long getTotalBonusBalance(final Map<String, Long> customerBalanceMap) {
		Long bonusBalance = 0L;
		bonusBalance += customerBalanceMap.getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L);
		bonusBalance += customerBalanceMap.getOrDefault("PLAYER_BALANCE_CASINO_BONUS_PENDING", 0L);
		
		return bonusBalance;
	}
	
	/**
	 * Convenience method to get total customer balance. 
	 * This includes escrow, real money, bonus money and pending bonus money.
	 * @param customerBalanceMap
	 * @return Total player balance
	 */
	private Long getTotalBalance(final Map<String, Long> customerBalanceMap) {
		Long totalCustomerBalance = 0L;
		// FIXME : LSPLAT-557, PLAT-1271 alignment on multibet balance response calculation with getCustomerBalanceWithError
		for (Entry<String, Long> balance: customerBalanceMap.entrySet()) {
			if (balance.getKey().equalsIgnoreCase("PLAYER_BALANCE") ||
			    balance.getKey().equalsIgnoreCase("PLAYER_BALANCE_CASINO_BONUS") ||
			    balance.getKey().equalsIgnoreCase("PLAYER_BALANCE_CASINO_BONUS_PENDING")
			) {
				totalCustomerBalance += balance.getValue();
			}
		}
		return totalCustomerBalance;
	}
	

	// No more need for zero value determination since we pass exactly what we want to process
	
	// Perform bonus active check
	
	// Check if it is a freespin
	
	// Check net balance of player before proceeding with balance adjustment ito bet
	
	// Check for outstanding rounds if a bonus balance is at its below zero limit. Don't cancel it and reject bet. Allow wins and rollbacks. Dispatch event notifying player of outstanding active rounds
	// Implement ability for support staff to force outstanding rounds to complete if for some reason it is not complete
	
	// Process balance adjustment components. Create an accounting method to process multiple account modifications in a single transaction.

	// FIXME: For rollbacks. Create accounting method to lookup the original transactions linked to the external transaction id.
	// Determine course of action for rollback based on wether bonus is still active or if we are using real money and bonus will need to be re-activated.
	// Modify playthrough where required.
	
	/**
	 * Get a map of player account balances for a specific player.
	 * @param request
	 * @return Returns a map containing all available player balances.
	 * The map keys are the account codes of the various player balance account types.
	 */
	public Map<String,Long> getCustomerBalanceMap(final BalanceAdjustmentRequest request) {

		Map<String, Long> balanceMap = new HashMap<>();
		
		try {
			Response<Map<String, Long>> balanceResponse = null;
			String domainName = request.getDomainName();
			String currency = cachingDomainClientService.retrieveDomainFromDomainService(domainName).getCurrency();
			String userGuid = request.getUserGuid();

			balanceResponse = getAccountingService().getByAccountType(domainName, "PLAYER_BALANCE", currency, userGuid);
			if (balanceResponse.getStatus() != Status.OK){
				balanceMap.put("PLAYER_BALANCE", 0L);
				balanceResponse = new Response<>();
			} else {
				balanceMap.putAll(balanceResponse.getData());
			}

			if (request.getPlayerRewardTypeHistoryId() != null) {
				log.debug("We have a reward from the comps engine, getting player reward account balances");
				Response<Map<String, Long>> rewardBalanceResponse = getAccountingService()
						.getByAccountType(domainName, "PLAYER_BALANCE_REWARD", currency, userGuid);

				if (rewardBalanceResponse.getStatus() == Status.OK) {
					balanceMap.putAll(rewardBalanceResponse.getData());
				}
			}
		} catch (Exception e) {
			log.error("Error getting player balances from accounting service module.", e);
			return null;
		}
		
		return balanceMap;
//		balance = balanceResponse.getData().getOrDefault("PLAYER_BALANCE", 0L);
//		balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L);		
//		balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS_PENDING", 0L);
	}
	
	/**
	 * Fetches the mapped feign client for the accounting service
	 * @return AccountingClient
	 */
	private AccountingClient getAccountingService() {
		AccountingClient cl = null;
		try {
			cl = services.target(AccountingClient.class, "service-accounting", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return cl;
	}
	
	/**
	 * Provides a list of player pending bonuses ordered so that balance affecting bonuses are first.
	 * Freespin only bonuses are last in the list
	 * @param playerGuid
	 * @return List of player pending bonuses
	 */
	private List<PlayerBonusPending> getSortedPlayerBonusPendingList(final String playerGuid) {
		List<PlayerBonusPending> pbpList = playerBonusPendingRepository.findByPlayerGuidOrderByCreatedDateAsc(playerGuid);
		
		//Add bonuses without balance adjustment component to the end of the list for processing
		List<PlayerBonusPending> tmpPbpList = new ArrayList<>();
		for (PlayerBonusPending pbp : pbpList) {
			if (pbp.getBonusAmount() <= 0L && pbp.getTriggerAmount() <= 0L) {
				tmpPbpList.add(pbp);
				pbpList.remove(pbp);
			}
		}
		pbpList.addAll(tmpPbpList);
		
		return pbpList;
	}
	
	/**
	 * Performs a potential pending bonus activation if it is required.
	 * @param playerBonus
	 * @return Pending bonus activation outcome
	 */
	private EPendingBonusWorkflowStatus activateNextPendingBonus(PlayerBonus playerBonus) {
		
		List<PlayerBonusPending> pbpList = getSortedPlayerBonusPendingList(playerBonus.getPlayerGuid());
		if (pbpList.isEmpty()) return EPendingBonusWorkflowStatus.NO_PENDING_BONUS_LEFT;
		
		if (isBonusAllowedToEnd(playerBonus)) {
			try {
				casinoBonusTransactionService.movePendingBonusToActive(playerBonus, pbpList.get(0));
			} catch (Exception e) {
				log.error("Unable to perform pending bonus activation. playerBonusPendingList: " + pbpList + " | playerBonus: " + playerBonus);
				return EPendingBonusWorkflowStatus.ERROR_ACTIVATING_PENDING_BONUS;
			}
			return EPendingBonusWorkflowStatus.ACTIVATED_PENDING_BONUS;
		} else {
			return EPendingBonusWorkflowStatus.NO_PENDING_ACTIVATION_PERFORMED;
		}
	}
	
	/**
	 * Perform a check on outstanding freespins and unfinished rounds on bonus.
	 * Sends events to the user to notify them of the outstanding free spins and/or unfinished rounds
	 * @param playerBonus
	 * @return true if the bonus is allowed to end
	 */
	private boolean isBonusAllowedToEnd(PlayerBonus playerBonus) {
		boolean incompleteFreespins = casinoBonusFreespinService.hasIncompleteFreespins(playerBonus);
		
		if (incompleteFreespins) {
			log.warn("Player with unused freespins detected at bonus end scenario." + playerBonus);
			try {
				//TODO: Rework the sending to be more descriptive or use another method for sending
				casinoBonusService.registerUserEventIncompleteRounds(
						playerBonus.getCurrent().getBonus().getDomain().getName(), 
						playerBonus.getPlayerGuid().split("/")[1], 
						casinoBonusService.playerBonusDisplay(playerBonus.getPlayerGuid(), null));
			} catch (JsonProcessingException e) {
				log.error("Failure in dispatching incomplete bonus event: " + playerBonus, e);
			}
		}
		
		boolean unfinishedRounds = bonusRoundTrackService.isUnfinishedRoundsOnBonus(playerBonus.getCurrent());
		
		if (unfinishedRounds) {
			log.warn("Player with unfinished rounds detected at bonus end scenario." + playerBonus);
			try {
				//TODO: Rework the sending to be more descriptive or use another method for sending
				casinoBonusService.registerUserEventIncompleteRounds(
						playerBonus.getCurrent().getBonus().getDomain().getName(), 
						playerBonus.getPlayerGuid().split("/")[1], 
						casinoBonusService.playerBonusDisplay(playerBonus.getPlayerGuid(), null));
			} catch (JsonProcessingException e) {
				log.error("Failure in dispatching incomplete bonus event: " + playerBonus, e);
			}
		}
		return !(unfinishedRounds && incompleteFreespins);
	}
}
