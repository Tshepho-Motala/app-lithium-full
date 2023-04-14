package lithium.service.casino.client.objects.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentAccountEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class BalanceAdjustmentRequest extends Request {
	private static final long serialVersionUID = 1L;

	private String userGuid;
	private String roundId;
	private Long externalTimestamp;
	private String gameGuid;
	private Boolean roundFinished;
	private String gameSessionId;
	private String transactionId;
	private String transactionTiebackId;
	@Builder.Default
	private Boolean bonusTran = false;
	private Integer bonusId;
	private List<BalanceAdjustmentComponent> adjustmentComponentList;
	private String currencyCode;
	@Builder.Default
	private Boolean allowNegativeBalanceAdjustment = false;
	@Builder.Default
	private Boolean realMoneyOnly = false;
	@Builder.Default
	private Boolean performAccessChecks = true; // Used to flag if the limit/user status checking
	@Builder.Default
	private Boolean persistRound = false;
	private Long sessionId;

	private Long playerRewardTypeHistoryId;  //service-reward
	private Long playerBonusHistoryId;  //service-casino

	public BalanceAdjustmentRequest() {
		this.bonusTran = false;
		this.allowNegativeBalanceAdjustment = false;
		this.realMoneyOnly = false;
		this.performAccessChecks = true;
	}

	/**
	 * Convenience method for providing the total debit cents contained in the adjustment component list.
	 * Bets are debit type transactions.
	 * @return total debit amount cents
	 */
	@JsonIgnore
	public Long getTotalDebitAmountCents() {
		Long totalDebit = 0L;
		for (final BalanceAdjustmentComponent adjustment: adjustmentComponentList) {
			if (adjustment.getAdjustmentType().getAccountEffect() == EBalanceAdjustmentAccountEffect.DEBIT) {
				totalDebit += adjustment.getAmountAbs();
			}
		}
		return totalDebit;
	}

	/**
	 * Convenience method for providing the total credit cents contained in the adjustment component list.
	 * Wins are credit type transactions.
	 * @return total credit amount cents
	 */
	@JsonIgnore
	public Long getTotalCreditAmountCents() {
		Long totalDebit = 0L;
		for (final BalanceAdjustmentComponent adjustment: adjustmentComponentList) {
			if (adjustment.getAdjustmentType().getAccountEffect() == EBalanceAdjustmentAccountEffect.CREDIT) {
				totalDebit += adjustment.getAmountAbs();
			}
		}
		return totalDebit;
	}

	/**
	 * Convenience method for providing the total wagered bets contained in the adjustment component list.
	 * @return total wagered bet amount cents
	 */
	@JsonIgnore
	public Long getTotalWageredBetAmountCents() {
		return getAdjustmentComponentList().stream()
		.filter(component -> component.getAdjustmentType().isWageredBet())
		.mapToLong(BalanceAdjustmentComponent::getAmount)
		.sum();
	}
}
