package lithium.service.reward.client.enums;

import lithium.transaction.IBalanceAdjustmentAccountEffect;
import lithium.transaction.IBalanceAdjustmentComponentType;

public enum RewardBalanceAdjustmentComponentType implements IBalanceAdjustmentComponentType {

    REWARD_BET(RewardBalanceAdjustmentAccountEffect.DEBIT, true),
    REWARD_BET_REVERSAL(RewardBalanceAdjustmentAccountEffect.CREDIT, false),
    REWARD_WIN(RewardBalanceAdjustmentAccountEffect.CREDIT, false),
    REWARD_WIN_REVERSAL(RewardBalanceAdjustmentAccountEffect.DEBIT, false),
    REWARD_LOSS(RewardBalanceAdjustmentAccountEffect.DEBIT, false);

    private RewardBalanceAdjustmentAccountEffect accountEffect;

    private boolean wageredBet;

    RewardBalanceAdjustmentComponentType(RewardBalanceAdjustmentAccountEffect accountEffect, boolean wageredBet) {
        this.accountEffect = accountEffect;
        this.wageredBet = wageredBet;
    }

    @Override
    public IBalanceAdjustmentAccountEffect getAccountEffect() {
        return accountEffect;
    }

    @Override
    public boolean getWageredBet() {
        return wageredBet;
    }
}
