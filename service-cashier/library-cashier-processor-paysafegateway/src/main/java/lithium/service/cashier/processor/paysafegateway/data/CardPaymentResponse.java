package lithium.service.cashier.processor.paysafegateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class CardPaymentResponse {
	private List<Link> links;
	private String id;
	private String merchantRefNum;
	private Date txnTime;
	private String status;
	private long amount;
	private boolean settleWithAuth;
	private boolean preAuth;
	private long availableToSettle;
	private Card card;
	private String authCode;
	private BillingDetails billingDetails;
	private MerchantDescriptor merchantDescriptor;
	private String currencyCode;
	private String avsResponse;
	private String cvvVerification;
	private List<Settlement> settlements;
	private Authentication authentication;
}
