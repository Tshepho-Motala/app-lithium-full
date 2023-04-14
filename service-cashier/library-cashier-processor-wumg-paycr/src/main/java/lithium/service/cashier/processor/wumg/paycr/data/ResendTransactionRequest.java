package lithium.service.cashier.processor.wumg.paycr.data;

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
public class ResendTransactionRequest {

	private String transactionId;
	private String extTransId;
	private String player;
	private String trackingNumber;
	private String amount;
	private String comments;
	private String userName;
	private String password;
	
	
}
