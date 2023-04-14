package lithium.service.casino.client.data;

import lombok.Getter;

import java.io.Serializable;

/**
 * Enumeration of the possible transaction component types originating at the remote casino provider.
 * The component types are also linked to the effect it would have on the player balance.
 *
 */
public enum EBalanceAdjustmentComponentType implements Serializable {
	CASINO_BET(EBalanceAdjustmentAccountEffect.DEBIT, true),
	VIRTUAL_BET(EBalanceAdjustmentAccountEffect.DEBIT, true),
	CASINO_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_VOID(EBalanceAdjustmentAccountEffect.CREDIT, false),
	VIRTUAL_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	VIRTUAL_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_WIN_JACKPOT(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_NEGATIVE_BET(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_BET(EBalanceAdjustmentAccountEffect.DEBIT, false),
	VIRTUAL_FREE_BET(EBalanceAdjustmentAccountEffect.DEBIT, false),
	VIRTUAL_FREE_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	VIRTUAL_FREE_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_WIN_JACKPOT(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_REAL_MONEY_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_FEATURE_BET(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_FEATURE_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_BET_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	VIRTUAL_BET_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_WIN_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_WIN_JACKPOT_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_NEGATIVE_BET_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_BET_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_WIN_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_WIN_JACKPOT_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_FREEROUND_FEATURE_BET_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_FREEROUND_FEATURE_WIN_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_TRANSFER_FROM_REAL_TO_ESCROW(EBalanceAdjustmentAccountEffect.DEBIT, false), //TODO: Move these transfer types to their own enum, they actually don't belong here
	CASINO_TRANSFER_FROM_ESCROW_TO_BONUS(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_TRANSFER_FROM_ESCROW_TO_REAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_TRANSFER_FROM_PENDING_TO_BONUS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_TRANSFER_FROM_REAL_TO_PENDING(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_TRANSFER_FROM_PENDING_TO_REAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_TRANSFER_FROM_BONUS_TO_REAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_TRANSFER_FROM_BONUS_TO_EXCESS(EBalanceAdjustmentAccountEffect.CREDIT, false),
	SPORTS_BET(EBalanceAdjustmentAccountEffect.DEBIT, true),
	SPORTS_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	SPORTS_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	SPORTS_RESETTLEMENT(EBalanceAdjustmentAccountEffect.DEBIT, false),
	SPORTS_FREE_BET(EBalanceAdjustmentAccountEffect.DEBIT, false),
	SPORTS_FREE_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	SPORTS_FREE_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false),
	SPORTS_FREE_RESETTLEMENT(EBalanceAdjustmentAccountEffect.DEBIT, false),
	SPORTS_RESERVE_RETURN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	SPORTS_NEGATIVE_BALANCE_ADJUSTMENT(EBalanceAdjustmentAccountEffect.CREDIT, false),
	JACKPOT_ACCRUAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	JACKPOT_ACCRUAL_CANCEL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_ADHOC_CREDIT(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_ADHOC_DEBIT(EBalanceAdjustmentAccountEffect.DEBIT, true),

	//TODO: Review all bonus/freeround above.
	// This is a special game, that has been marked as a "free game"
	CASINO_BET_FREEGAME(EBalanceAdjustmentAccountEffect.DEBIT, true),
	CASINO_BET_FREEGAME_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_WIN_FREEGAME(EBalanceAdjustmentAccountEffect.CREDIT, false),
	CASINO_WIN_FREEGAME_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	CASINO_LOSS_FREEGAME(EBalanceAdjustmentAccountEffect.DEBIT, false),

	//TODO: Needs to be moved out to client-service-reward.  See lithium.transaction.IBalanceAdjustmentComponentType 
	REWARD_BET(EBalanceAdjustmentAccountEffect.DEBIT, true),
	REWARD_BET_REVERSAL(EBalanceAdjustmentAccountEffect.CREDIT, false),
	REWARD_WIN(EBalanceAdjustmentAccountEffect.CREDIT, false),
	REWARD_WIN_REVERSAL(EBalanceAdjustmentAccountEffect.DEBIT, false),
	REWARD_LOSS(EBalanceAdjustmentAccountEffect.DEBIT, false)
	;

	@Getter
	private EBalanceAdjustmentAccountEffect accountEffect;

	@Getter
	private boolean wageredBet;
	
	EBalanceAdjustmentComponentType(EBalanceAdjustmentAccountEffect accountEffect, boolean wageredBet) {
		this.accountEffect = accountEffect;
		this.wageredBet = wageredBet;
	}
}
