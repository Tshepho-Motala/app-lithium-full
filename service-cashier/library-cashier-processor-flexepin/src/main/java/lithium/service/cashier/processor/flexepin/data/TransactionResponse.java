package lithium.service.cashier.processor.flexepin.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
	/*	 
	 */
	Number value;
	Number cost;
	String result;
	String result_description;
	String transaction_id;
	String trans_no;
	String serial;
	String currency;
	String description;
	String ean;
	String status;
	String residual_value;
	long amount;
	long amountCents;

}
