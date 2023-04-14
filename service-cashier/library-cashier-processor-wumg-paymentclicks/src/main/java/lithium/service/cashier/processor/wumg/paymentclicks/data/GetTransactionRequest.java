package lithium.service.cashier.processor.wumg.paymentclicks.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetTransactionRequest {

	private String user;
	private String password;
	private String transaction_id;
	
}
