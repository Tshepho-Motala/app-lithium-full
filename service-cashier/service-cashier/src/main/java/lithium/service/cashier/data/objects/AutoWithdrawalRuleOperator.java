package lithium.service.cashier.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AutoWithdrawalRuleOperator {
	private Integer id;
	private String operator;
	private String displayName;
}
