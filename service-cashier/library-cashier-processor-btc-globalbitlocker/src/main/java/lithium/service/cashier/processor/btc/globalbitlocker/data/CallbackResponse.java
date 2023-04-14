package lithium.service.cashier.processor.btc.globalbitlocker.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CallbackResponse {
	private String hash;
	private Long transactionId;
	private String id;
	private String amount;
	private String bitcoinAmount;
	private String status;
}
