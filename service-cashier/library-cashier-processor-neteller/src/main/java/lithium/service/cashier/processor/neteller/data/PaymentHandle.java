package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.Action;
import lithium.service.cashier.processor.neteller.data.enums.Status;
import lithium.service.cashier.processor.neteller.data.enums.Usage;
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
public class PaymentHandle {
	private String id;
	private String merchantRefNum;
	private String transactionType;
	private String accountId;
	private Status status;
	private Usage usage;
	private String paymentType;
	private Neteller neteller;
	private Action action;
	private String executionMode;
	private Long amount;
	private String currencyCode;
	private String paymentHandleTokenFrom;
	private String paymentHandleToken;
	private Profile profile;
	private BillingDetails billingDetails;
	private List<Link> returnLinks;
	private List<Link> links;
	private String customerIp;
	private Long timeToLiveSeconds;
	private Boolean dupCheck;
	private Boolean liveMode;
	private GatewayResponse gatewayResponse;
	private Date txnTime;
	private Date updatedDate;
	private Date statusTime;
}
