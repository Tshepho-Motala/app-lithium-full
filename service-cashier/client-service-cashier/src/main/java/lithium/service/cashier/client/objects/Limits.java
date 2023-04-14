package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Limits {
	private Long id;
	
	private Long minAmount;
	private Long maxAmount;

	private Long minFirstTransactionAmount;
	private Long maxFirstTransactionAmount;

	private Long maxAmountDay;
	private Long maxAmountWeek;
	private Long maxAmountMonth;
	
	private Long maxTransactionsDay;
	private Long maxTransactionsWeek;
	private Long maxTransactionsMonth;
}
