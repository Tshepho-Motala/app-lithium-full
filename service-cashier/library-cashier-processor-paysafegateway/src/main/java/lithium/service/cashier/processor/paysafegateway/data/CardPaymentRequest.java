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
public class CardPaymentRequest {
	private String merchantRefNum;
	private long amount;
	private boolean settleWithAuth;
	private Card card;
	private BillingDetails billingDetails;
}
