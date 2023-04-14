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
public class VerificationResponse {
	private String id;
	private String merchantRefNum;
	private Profile profile;
	private BillingDetails billingDetails;
	private String customerIp;
	private Boolean dupCheck;
	private String description;
	private Card card;
	private String authCode;
	private Date txnTime;
	private String currencyCode;
	private String avsResponse;
	private String cvvVerification;
	private String status;
	private List<Link> links;
	private Error error;
}
