package lithium.service.cashier.frontend.paysafegateway.data.objects;

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
public class Payment {
	private String apiKey;
	private String environment;
	private long accountId;
	private boolean useThreeDSecure;
	private boolean useThreeDSecureVersion2;
	private long amountCents;
	private String amountFormatted;
	private String currencyCode;
	private String transactionId;
	private String token;
}
