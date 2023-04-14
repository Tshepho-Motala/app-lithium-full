package lithium.service.casino.service.util;

import lithium.casino.CasinoTransactionLabels;
import lithium.service.accounting.enums.ConstraintValidationType;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.ConstraintValidation;
import lithium.service.accounting.objects.RollbackRequestComponent;
import lithium.service.casino.CasinoAccountTypeCodes;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.User;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdjustmentRequestFactory {

	@Autowired private CachingDomainClientService cachingDomainClientService;
	
	/**
	 * Constructs an accounting adjustment component from the given parameters.
	 * It is bonus-aware and will create the correct component when a bonus is active.
	 * @param request
	 * @param adjComponent
	 * @param labelManager
	 * @param playerBonus
	 * @return
	 * @throws Exception
	 */
	public AdjustmentRequestComponent constructAdjustmentRequestComponent(
			final BalanceAdjustmentRequest request,
			final BalanceAdjustmentComponent adjComponent,
			final LabelManager labelManager,
			final PlayerBonus playerBonus
	) throws Status550ServiceDomainClientException {
		boolean allowedToUseBonus = (playerBonus != null && playerBonus.getCurrent() != null && !request.getRealMoneyOnly());

		switch (adjComponent.getAdjustmentType()) {
			case CASINO_BET:
				if (allowedToUseBonus) {
					return createBonusBetRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyBetRequestComponent(request, adjComponent, labelManager);
				}
			case CASINO_BET_REVERSAL:
				//FIXME: Add bet reversal tran
				return createRealMoneyBetReversalComponent(request, adjComponent, labelManager);
			case CASINO_LOSS:
				return createCasinoLossComponent(request, adjComponent, labelManager);
			case CASINO_VOID:
				return createCasinoVoidComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_BET:
				return createFreespinBetRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_BET_REVERSAL:
				//FIXME: Add freeround bet reversal tran
				return createFreespinBetReversalComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_FEATURE_BET:
				//TODO: Create an actual transaction for this to be able to count actual frb spins and feature spins
				return createFreespinBetRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_FEATURE_BET_REVERSAL:
				//FIXME: Add freeround feature bet reversal tran
				return null;
			case CASINO_FREEROUND_FEATURE_WIN:
				return createFreespinWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_FEATURE_WIN_REVERSAL:
				//FIXME: Add freeround feature win reversal tran
				return null;
			case CASINO_FREEROUND_LOSS:
				return createRealMoneyFreespinLossRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_WIN:
				return createFreespinWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_WIN_JACKPOT:
				return createRealMoneyFreespinWinJackpotRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_REAL_MONEY_WIN:
				return createRealMoneyFreespinWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_WIN_REVERSAL:
				//FIXME: Add freeround win reversal tran
				return createRealMoneyFreespinRefundWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_FREEROUND_WIN_JACKPOT_REVERSAL:
				//FIXME: Add freeround win reversal tran
				return createRealMoneyFreespinRefundJackpotWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_NEGATIVE_BET:
				if (allowedToUseBonus) {
					return createBonusNegativeBetRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyNegativeBetRequestComponent(request, adjComponent, labelManager);
				}
			case CASINO_NEGATIVE_BET_REVERSAL:
				//FIXME: Add negative bet reversal tran
				return null;
			case CASINO_TRANSFER_FROM_ESCROW_TO_BONUS:
				return createTransferFromEscrowToBonusRequestComponent(request, adjComponent, labelManager);
			case CASINO_TRANSFER_FROM_ESCROW_TO_REAL:
				return createTransferFromEscrowToRealRequestComponent(request, adjComponent, labelManager);
			case CASINO_TRANSFER_FROM_REAL_TO_ESCROW:
				return createTransferToEscrowRequestComponent(request, adjComponent, labelManager);
			case CASINO_WIN:
				if (allowedToUseBonus) {
					return createBonusWinRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyWinRequestComponent(request, adjComponent, labelManager);
				}
			case CASINO_WIN_JACKPOT:
				if (allowedToUseBonus) {
					return createBonusWinJackpotRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyWinJackpotRequestComponent(request, adjComponent, labelManager);
				}
			case CASINO_WIN_REVERSAL:
				//FIXME: Add win reversal tran
				return createRealMoneyWinReversalComponent(request, adjComponent, labelManager);
			case CASINO_WIN_JACKPOT_REVERSAL:
				//FIXME: Add win reversal tran
				return createRealMoneyWinJackpotReversalComponent(request, adjComponent, labelManager);
			case JACKPOT_ACCRUAL:
				//TODO: Implement bonus handling for sports
				return createRealMoneyJackpotAccrualRequestComponent(request, adjComponent, labelManager);
			case JACKPOT_ACCRUAL_CANCEL:
				//TODO: Implement bonus handling for sports
				return createRealMoneyJackpotAccrualCancelRequestComponent(request, adjComponent, labelManager);
			case SPORTS_BET:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsBetRequestComponent(request, adjComponent, labelManager);
			case SPORTS_WIN:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsWinRequestComponent(request, adjComponent, labelManager);
			case SPORTS_LOSS:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsLossRequestComponent(request, adjComponent, labelManager);
			case SPORTS_RESETTLEMENT:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsResettlementRequestComponent(request, adjComponent, labelManager);
			case SPORTS_FREE_BET:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsFreeBetRequestComponent(request, adjComponent, labelManager);
			case SPORTS_FREE_WIN:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsFreeWinRequestComponent(request, adjComponent, labelManager);
			case SPORTS_FREE_LOSS:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsFreeLossRequestComponent(request, adjComponent, labelManager);
			case SPORTS_FREE_RESETTLEMENT:
				//TODO: Implement bonus handling for sports
				return createRealMoneySportsFreeResettlementRequestComponent(request, adjComponent, labelManager);
			case SPORTS_RESERVE_RETURN:
				return createRealMoneySportsReserveReturnRequestComponent(request, adjComponent, labelManager);
			case SPORTS_NEGATIVE_BALANCE_ADJUSTMENT:
				return createRealMoneySportsNegativeBalanceAdjustRequestComponent(request, adjComponent, labelManager);
			case CASINO_ADHOC_CREDIT:
				if (allowedToUseBonus) {
					return createBonusAdHocCreditRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyAdHocCreditRequestComponent(request, adjComponent, labelManager);
				}
			case CASINO_ADHOC_DEBIT:
				if (allowedToUseBonus) {
					return createBonusAdHocDebitRequestComponent(request, adjComponent, labelManager);
				} else {
					return createRealMoneyAdHocDebitRequestComponent(request, adjComponent, labelManager);
				}
				//Free Game Trans
			case CASINO_BET_FREEGAME:
				return createFreeGameBetRequestComponent(request, adjComponent, labelManager);
			case CASINO_BET_FREEGAME_REVERSAL:
				return createFreeGameBetReversalRequestComponent(request, adjComponent, labelManager);
			case CASINO_WIN_FREEGAME:
				return createFreeGameWinRequestComponent(request, adjComponent, labelManager);
			case CASINO_WIN_FREEGAME_REVERSAL:
				return createFreeGameWinReversalRequestComponent(request, adjComponent, labelManager);
			case CASINO_LOSS_FREEGAME:
				return createFreeGameLossRequestComponent(request, adjComponent, labelManager);
			default:
				break;
			}
		
		return null;
	}

	public AdjustmentRequestComponent createRealMoneySportsFreeBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_FREE_BET.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_FREE_BET.toString(),
				CasinoTranType.SPORTS_FREE_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsFreeResettlementRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_FREE_RESETTLEMENT.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_FREE_RESETTLEMENT.toString(),
				CasinoTranType.SPORTS_FREE_RESETTLEMENT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsFreeWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_FREE_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_FREE_WIN.toString(),
				CasinoTranType.SPORTS_FREE_WIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsFreeLossRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_FREE_LOSS.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_FREE_LOSS.toString(),
				CasinoTranType.SPORTS_FREE_LOSS.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_BET.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_BET.toString(),
				CasinoTranType.SPORTS_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsNegativeBalanceAdjustRequestComponent(
		BalanceAdjustmentRequest request,
		BalanceAdjustmentComponent adjComponent,
		LabelManager labelManager
	) throws
		Status550ServiceDomainClientException
	{
		return createAdjustmentRequestComponent(
			CasinoTranType.PLAYERBALANCE.toString(),
			CasinoTranType.PLAYERBALANCE.toString(),
			CasinoTranType.NEGATIVE_BALANCE_ADJUST.toString(),
			CasinoTranType.NEGATIVE_BALANCE_ADJUST.toString(),
			CasinoTranType.NEGATIVE_BALANCE_ADJUST.toString(),
			request,
			adjComponent.getAccountEffectAmount(),
			labelManager
		);
	}

	public AdjustmentRequestComponent createRealMoneySportsReserveReturnRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString(),
				CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString(),
				CasinoTranType.SPORTS_RESERVE_COMMIT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsResettlementRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_RESETTLEMENT.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_RESETTLEMENT.toString(),
				CasinoTranType.SPORTS_RESETTLEMENT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_WIN.toString(),
				CasinoTranType.SPORTS_WIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneySportsLossRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.SPORTS_LOSS.toString(), request.getProviderGuid()),
				CasinoTranType.SPORTS_LOSS.toString(),
				CasinoTranType.SPORTS_LOSS.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createRealMoneyBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createCasinoLossComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_LOSS.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_LOSS.toString(),
				CasinoTranType.CASINO_LOSS.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createCasinoVoidComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_VOID.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_VOID.toString(),
				CasinoTranType.CASINO_VOID.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		List<ConstraintValidation> constraintValidations = new ArrayList<>();
//		constraintValidations.add(
//				ConstraintValidation.builder()
//						.type(ConstraintValidationType.REQUIRED)
//						.accountCode(accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(),
//								request.getProviderGuid()))
//						.accountTypeCode(CasinoTranType.CASINO_BET.toString())
//						.labelName(CasinoTransactionLabels.TRAN_ID_LABEL)
//						.labelValue(adjComponent.getBetTransactionId())
//						.build()
//		);
//		if (component.getConstraintValidations() == null) {
//			component.setConstraintValidations(constraintValidations);
//		} else {
//			component.getConstraintValidations().addAll(constraintValidations);
//		}

		return component;
	}

	public AdjustmentRequestComponent createRealMoneyBetReversalComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}

	public AdjustmentRequestComponent createRealMoneyWinReversalComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}

	public AdjustmentRequestComponent createRealMoneyWinJackpotReversalComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_JACKPOT_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}

	public AdjustmentRequestComponent createBonusBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createFreespinBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET_FREESPIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createFreespinBetReversalComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET_FREESPIN_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}

	public AdjustmentRequestComponent createRealMoneyWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyWinJackpotRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_JACKPOT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyJackpotAccrualRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), //String accountCode,
				CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), //String accountTypeCode,
				accountCodeFromProviderGuidAndAccountType("JACKPOT_ACCRUALS", request.getProviderGuid()), //String contraAccountCode,
				"JACKPOT_ACCRUALS", //String contraAccountTypeCode,
				CasinoTranType.JACKPOT_ACCRUAL.toString(), //String transactionTypeCode,
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyJackpotAccrualCancelRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), //String accountCode,
				CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), //String accountTypeCode,
				accountCodeFromProviderGuidAndAccountType("JACKPOT_ACCRUALS", request.getProviderGuid()), //String contraAccountCode,
				"JACKPOT_ACCRUALS", //String contraAccountTypeCode,
				CasinoTranType.JACKPOT_ACCRUAL_CANCEL.toString(), //String transactionTypeCode,
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createBonusWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createBonusWinJackpotRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_JACKPOT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createFreespinWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_FREESPIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyFreespinWinJackpotRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_FREESPIN_JACKPOT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyFreespinWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_FREESPIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyFreespinLossRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_LOSS_FREESPIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_LOSS_FREESPIN.toString(),
				CasinoTranType.CASINO_LOSS_FREESPIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createRealMoneyNegativeBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_NEGATIVE_BET.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createBonusNegativeBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_NEGATIVE_BET.toString(), request.getProviderGuid()), 
				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createTransferToEscrowRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.PLAYER_BALANCE_CASINO_ESCROW.toString(), request.getProviderGuid()), 
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.TRANSFER_TO_CASINO_ESCROW.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createTransferFromEscrowToBonusRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.PLAYER_BALANCE_CASINO_ESCROW.toString(), request.getProviderGuid()), 
				CasinoTranType.PLAYER_BALANCE_CASINO_ESCROW.toString(),
				CasinoTranType.TRANSFER_FROM_CASINO_ESCROW.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createTransferFromEscrowToRealRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.PLAYER_BALANCE_CASINO_ESCROW.toString(), request.getProviderGuid()), 
				CasinoTranType.PLAYER_BALANCE_CASINO_ESCROW.toString(),
				CasinoTranType.TRANSFER_FROM_CASINO_ESCROW.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}
	
	public AdjustmentRequestComponent createTransferFromPendingToBonusRequestComponent(
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager,
			String domainName,
			String userGuid
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				false,
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.toString(), 
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.toString(),
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS_PENDING.toString(),
				adjComponent.getAccountEffectAmount(),
				labelManager,
				domainName,
				userGuid);
	}
	
	public AdjustmentRequestComponent createTransferFromBonusToRealRequestComponent(
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager,
			String domainName,
			String userGuid
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				false,
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), 
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				adjComponent.getAccountEffectAmount(),
				labelManager,
				domainName,
				userGuid);
	}
	
	public AdjustmentRequestComponent createTransferFromBonusToExcessRequestComponent(
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager,
			String domainName,
			String userGuid
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(),
				CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(),
				false,
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), 
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				adjComponent.getAccountEffectAmount(),
				labelManager,
				domainName,
				userGuid);
	}
//	private RollbackRequestComponent createRefundRealMoneyBetRequestComponent(CompleteTransaction originalTransaction, LabelManager labelManager) throws Exception {
//		return createRollbackRequestComponent(originalTransaction, labelManager);
//	}
//	
//	private AdjustmentRequestComponent createBonusRefundBetRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_BET.toString(),
//				CasinoTranType.CASINO_BET_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
//	
//	private AdjustmentRequestComponent createFreespinRefundBetRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_BET.toString(),
//				CasinoTranType.CASINO_BET_FREESPIN_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
//	
//	private AdjustmentRequestComponent createRealMoneyRefundWinRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYERBALANCE.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_WIN.toString(),
//				CasinoTranType.CASINO_WIN_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
//	
//	private AdjustmentRequestComponent createBonusRefundWinRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_WIN.toString(),
//				CasinoTranType.CASINO_WIN_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
//	
	private AdjustmentRequestComponent createRealMoneyFreespinRefundWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws Status550ServiceDomainClientException {
		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_FREESPIN_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}

	private AdjustmentRequestComponent createRealMoneyFreespinRefundJackpotWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		AdjustmentRequestComponent component = createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_FREESPIN_JACKPOT_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);

//		addReversalConstraintValidations(component, adjComponent.getReversalBetTransactionId());

		return component;
	}
//	
//	private AdjustmentRequestComponent createRealMoneyRefundNegativeBetRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYERBALANCE.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_NEGATIVE_BET.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
//				CasinoTranType.CASINO_NEGATIVE_BET_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
//	
//	private AdjustmentRequestComponent createBonusRefundNegativeBetRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager) throws Exception {
//		return createAdjustmentRequestComponent(
//				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
//				CasinoTranType.PLAYERBALANCE.toString(),
//				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_NEGATIVE_BET.toString(), request.getProviderGuid()), 
//				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
//				CasinoTranType.CASINO_NEGATIVE_BET_ROLLBACK.toString(),
//				request,
//				adjComponent.getAccountEffectAmount(),
//				labelManager);
//	}
	
	public String accountCodeFromProviderGuidAndAccountType(String accountType, String providerGuid) {
		int lastDash = providerGuid.lastIndexOf('-');
		return accountType + "_" + providerGuid.substring(lastDash+1).toUpperCase();
	}

//	private String accountCodeFromProviderGuidAndAccountTypeAndTranType(String accountType, String providerGuid, String tranType) {
//		int lastDash = providerGuid.lastIndexOf('-');
//		int typeStart = accountType.lastIndexOf("_");
//		return tranType + "_" + providerGuid.substring(lastDash+1).toUpperCase()+ "_" + accountType.substring(typeStart+1).toUpperCase();
//	}
	
	private AdjustmentRequestComponent createAdjustmentRequestComponent(
			String accountCode, 
			String accountTypeCode,
			String contraAccountCode, 
			String contraAccountTypeCode, 
			String transactionTypeCode, 
			BalanceAdjustmentRequest request, 
			long amountCents, 
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		return createAdjustmentRequestComponent(
				accountCode,
				accountTypeCode,
				request.getAllowNegativeBalanceAdjustment(),
				contraAccountCode, 
				contraAccountTypeCode,
				transactionTypeCode, 
				amountCents, 
				labelManager,
				request.getDomainName(),
				request.getUserGuid());
	}
	
	private AdjustmentRequestComponent createAdjustmentRequestComponent(
			String accountCode, 
			String accountTypeCode,
			boolean allowNegativeAdjust,
			String contraAccountCode, 
			String contraAccountTypeCode, 
			String transactionTypeCode, 
			long amountCents, 
			LabelManager labelManager,
			String domainName,
			String userGuid
	) throws
			Status550ServiceDomainClientException {
		// TODO This is only a mitigation fix for PLAT-12459. Needs a proper fix
		if (!allowNegativeAdjust && accountCode.equals("PLAYER_BALANCE_REWARD_RX_FREESPIN_FREEGAME")) {
			allowNegativeAdjust = true;
		}

		return AdjustmentRequestComponent.builder()
		.accountCode(accountCode)
		.accountTypeCode(accountTypeCode)
		.allowNegativeAdjust(allowNegativeAdjust)
		.amountCents(amountCents)
		.authorGuid(User.SYSTEM_GUID)
		.contraAccountCode(contraAccountCode)
		.contraAccountTypeCode(contraAccountTypeCode)
		.currencyCode(cachingDomainClientService.retrieveDomainFromDomainService(domainName).getCurrency())
		.date(DateTime.now())
		.domainName(domainName)
		.labels(labelManager.getLabelArray())
		.ownerGuid(userGuid)
		.transactionTypeCode(transactionTypeCode)
		.build();
	}
	
	private RollbackRequestComponent createRollbackRequestComponent(
			CompleteTransaction originalTransaction,
			LabelManager labelManager) throws Exception {
		
		//TODO: Add whatever else is required in here to perform a balance adjustment for the rollback request
		//TODO: Also create a lookup for previous execution of the rollback. This will be possible if a label is added to indicate the rollback_tran_id on the original transaction
		return RollbackRequestComponent.builder()
		.allowNegativeAdjust(true)
		.authorGuid(User.SYSTEM_GUID)
		.labels(labelManager.getLabelArray())
//		.internalTransactionId(internalTransactionId)
		.originalTransaction(originalTransaction)
		.build();
	}

	public AdjustmentRequestComponent createBonusAdHocCreditRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_ADHOC_CREDIT.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_ADHOC_CREDIT.toString(),
				CasinoTranType.CASINO_ADHOC_CREDIT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyAdHocCreditRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_ADHOC_CREDIT.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_ADHOC_CREDIT.toString(),
				CasinoTranType.CASINO_ADHOC_CREDIT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createBonusAdHocDebitRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_ADHOC_DEBIT.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_ADHOC_DEBIT.toString(),
				CasinoTranType.CASINO_ADHOC_DEBIT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createRealMoneyAdHocDebitRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_ADHOC_DEBIT.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_ADHOC_DEBIT.toString(),
				CasinoTranType.CASINO_ADHOC_DEBIT.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	// TODO: Need to validate that reversalBetTransactionId is present in the BalanceAdjustmentComponent if adjustmentType
	//		 is one of the reversal types.
	private void addReversalConstraintValidations(AdjustmentRequestComponent component,
			String reversalBetTransactionId) {
		List<ConstraintValidation> constraintValidations = new ArrayList<>();
		constraintValidations.add(
				ConstraintValidation.builder()
						.type(ConstraintValidationType.REQUIRED)
						.accountCode(component.getContraAccountCode())
						.accountTypeCode(component.getContraAccountTypeCode())
						.labelName(CasinoTransactionLabels.TRAN_ID_LABEL)
						.labelValue(reversalBetTransactionId)
						.build());
		if (component.getConstraintValidations() == null) {
			component.setConstraintValidations(constraintValidations);
		} else {
			component.getConstraintValidations().addAll(constraintValidations);
		}
	}

	public AdjustmentRequestComponent createFreeGameWinRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN_FREEGAME.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createFreeGameWinReversalRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {
		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_WIN_FREEGAME.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createFreeGameBetRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_FREEGAME.toString(),
				CasinoTranType.PLAYER_BALANCE_FREEGAME.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET_FREEGAME.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent createFreeGameBetReversalRequestComponent(
			BalanceAdjustmentRequest request,
			BalanceAdjustmentComponent adjComponent,
			LabelManager labelManager
	) throws
			Status550ServiceDomainClientException {

		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYER_BALANCE_FREEGAME.toString(),
				CasinoTranType.PLAYER_BALANCE_FREEGAME.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_BET_FREEGAME.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_BET_ROLLBACK.toString(),
				CasinoTranType.CASINO_BET_ROLLBACK.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	private AdjustmentRequestComponent createFreeGameLossRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent adjComponent, LabelManager labelManager)
	throws Status550ServiceDomainClientException
	{
		return createAdjustmentRequestComponent(
				CasinoTranType.PLAYERBALANCE.toString(),
				CasinoTranType.PLAYERBALANCE.toString(),
				accountCodeFromProviderGuidAndAccountType(CasinoTranType.CASINO_LOSS_FREEGAME.toString(), request.getProviderGuid()),
				CasinoTranType.CASINO_LOSS.toString(),
				CasinoTranType.CASINO_LOSS.toString(),
				request,
				adjComponent.getAccountEffectAmount(),
				labelManager);
	}

	public AdjustmentRequestComponent constructRewardAdjustmentRequestComponent(BalanceAdjustmentRequest request, BalanceAdjustmentComponent component, LabelManager labelManager)
	throws Status550ServiceDomainClientException
	{
		String transactionType = component.getAdjustmentType().toString();
		String accountCode = "";
		String accountTypeCode = "";
		String contraAccountCode = "";
		String contraAccountTypeCode = "";
		switch (component.getAdjustmentType()) {
			case REWARD_LOSS:
				accountCode = "PLAYER_BALANCE";
				accountTypeCode = "PLAYER_BALANCE";
				contraAccountCode = "REWARD_LOSS_" + component.getAccountCodeSuffix();
				contraAccountTypeCode = "REWARD_LOSS";
				break;
			case REWARD_BET:
				accountCode = "PLAYER_BALANCE_REWARD_" + component.getAccountCodeSuffix();
				accountTypeCode = "PLAYER_BALANCE_REWARD";
				contraAccountCode = "REWARD_BET_" + component.getAccountCodeSuffix();
				contraAccountTypeCode = "REWARD_BET";
				break;
			case REWARD_BET_REVERSAL:
				accountCode = "PLAYER_BALANCE_REWARD_" + component.getAccountCodeSuffix();
				accountTypeCode = "PLAYER_BALANCE_REWARD";
				contraAccountCode = "REWARD_BET_" + component.getAccountCodeSuffix();
				contraAccountTypeCode = "REWARD_BET";
				transactionType = CasinoTranType.REWARD_BET_ROLLBACK.toString();
//				addReversalConstraintValidations(component, component.getReversalBetTransactionId()); //TODO:
				break;
			case REWARD_WIN:
				accountCode = "PLAYER_BALANCE";
				accountTypeCode = "PLAYER_BALANCE";
				contraAccountCode = "REWARD_WIN_" + component.getAccountCodeSuffix();
				contraAccountTypeCode = "REWARD_WIN";
				break;
			case REWARD_WIN_REVERSAL:
				accountCode = "PLAYER_BALANCE";
				accountTypeCode = "PLAYER_BALANCE";
				contraAccountCode = "REWARD_WIN_" + component.getAccountCodeSuffix();
				contraAccountTypeCode = "REWARD_WIN";
				transactionType = CasinoTranType.REWARD_WIN_ROLLBACK.toString();
				break;
		}
//		labelManager.addLabel(LabelManager.TRANSACTION_ID, component.getTransactionIdLabelOverride());
		return createAdjustmentRequestComponent(
			accountCode,
			accountTypeCode,
			contraAccountCode,
			contraAccountTypeCode,
			transactionType,
			request,
			component.getAccountEffectAmount(),
			labelManager
		);
	}
}
