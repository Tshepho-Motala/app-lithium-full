package lithium.service.casino.client.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This component is used to identify transaction components that form part of a single casino adjustment
 * transaction that was initiated from the remote casino provier.
 */

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BalanceAdjustmentComponent {

	public BalanceAdjustmentComponent(
			EBalanceAdjustmentComponentType adjustmentType,
			Long amount,
			String betTransactionId,
			String transactionIdLabelOverride
	) {
		this.adjustmentType = adjustmentType;
		this.amount = amount;
		this.betTransactionId = betTransactionId;
		this.transactionIdLabelOverride = transactionIdLabelOverride;
		this.additionalReference = null;
	}
	
	@Setter @Getter
	private EBalanceAdjustmentComponentType adjustmentType;
	
	@Setter
	private Long amount;
	
	@Setter @Getter
	private String betTransactionId; // The casino provider's bet reference for the round

	@Setter @Getter
	private String transactionIdLabelOverride; // When present, it will replace the transaction id label being used in accounting

	@Getter @Setter
	private String additionalReference;

	@Getter @Setter
	private String reversalBetTransactionId; //transfer cancel / rollback

	@Getter @Setter
	private String[] labelValues;

	private String accountCodeSuffix;
	
	/**
	 * Returns the absolute value of the amount. Only positive values are used in processing. 
	 * The adjustmentType is used to determine debit or credit.
	 * @return abs value of amount
	 */
	public Long getAmountAbs() {
		if (amount != null) {
			return Math.abs(amount);
		}
		return amount;
	}
	
	/**
	 * Convenience method that returns the amount with signage based on the adjustment type's account effect.
	 * Will return negative for DEBIT and positive for CREDIT type.
	 * @return signed amount
	 */
	public Long getAccountEffectAmount() {
		if (amount != null) {
			Long accountModAmount = getAmountAbs() * getAdjustmentType().getAccountEffect().getAbsValueMultiplier();
			
			return accountModAmount;
		}
		return null;
	}
}
