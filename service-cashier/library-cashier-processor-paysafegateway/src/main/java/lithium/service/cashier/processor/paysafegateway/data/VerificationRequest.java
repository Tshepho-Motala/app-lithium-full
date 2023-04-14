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
public class VerificationRequest {
	private Card card;
	private String merchantRefNum;
	private BillingDetails billingDetails;
}
