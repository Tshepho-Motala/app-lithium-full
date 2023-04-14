package lithium.service.cashier.processor.paysafegateway.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionToken {
	private String transactionId;
	private String token;
	private String userAgent;
	private String userIp;
	private IframeError iframeError;
}
