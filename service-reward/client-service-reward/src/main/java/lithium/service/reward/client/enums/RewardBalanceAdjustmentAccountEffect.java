package lithium.service.reward.client.enums;

import lithium.transaction.IBalanceAdjustmentAccountEffect;

public enum RewardBalanceAdjustmentAccountEffect implements IBalanceAdjustmentAccountEffect {
    CREDIT(1L),
    DEBIT(-1L);

    private long multiplier;

    RewardBalanceAdjustmentAccountEffect(long multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public long getAbsValueMultiplier() {
        return multiplier;
    }
}
