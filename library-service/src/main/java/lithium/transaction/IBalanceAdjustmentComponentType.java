package lithium.transaction;

/**
 * Future use, don't have time now to implement this.
 * See comment on lithium.service.casino.client.data.EBalanceAdjustmentComponentType#REWARD_BET
 */

public interface IBalanceAdjustmentComponentType {

  IBalanceAdjustmentAccountEffect getAccountEffect();

  boolean getWageredBet();
}
