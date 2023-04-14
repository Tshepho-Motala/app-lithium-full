package lithium.service.casino.client.objects.response;

import java.io.Serializable;
import java.util.ArrayList;

import lithium.service.casino.client.data.BalanceAdjustmentResponseComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Unified casino adjustment response.
 * The relevant bonus history id is included in the response for a tie-back for providers that require
 * the casino service to link a transaction to a previously processed transaction.
 * Examples of that would be certain bonus rounds and possibly rollback transactions.
 * @author Chris
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BalanceAdjustmentResponse implements Serializable {
	private EBalanceAdjustmentResponseStatus result;
//	private String result;
//	private String code;
//	private String description;
	private Long balanceCents;
	private Long bonusBetPercentage;
	private Long bonusWinPercentage;
	private String playerBonusHistoryId;
	private ArrayList<BalanceAdjustmentResponseComponent> adjustmentResponseComponentList;
}
