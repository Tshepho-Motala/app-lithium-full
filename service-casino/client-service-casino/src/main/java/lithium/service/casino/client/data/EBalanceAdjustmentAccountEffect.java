package lithium.service.casino.client.data;

import lombok.Getter;

import java.io.Serializable;

/**
 * This is used in conjunction with the balance adjustment component.
 * It is used as an indicator of the expected effect of an adjustment transaction on the player balance.
 * @author Chris
 *
 */
public enum EBalanceAdjustmentAccountEffect implements Serializable {
	CREDIT(1L),
	DEBIT(-1L);
	
	@Getter
	private long absValueMultiplier;
	
	private EBalanceAdjustmentAccountEffect(long absValueMultiplier) {
		this.absValueMultiplier = absValueMultiplier;
	}
}
