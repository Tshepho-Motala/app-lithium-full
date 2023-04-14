package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Payment {
	private String id;
	private String merchantRefNum;
	private Long amount;
	private Boolean dupCheck;
	private Boolean settleWithAuth;
	private String paymentHandleToken;
	private Long availableToSettle;
	private String customerIp;
	private String description;
	private Date txnTime;
	private String paymentType;
	private Status status;
	private String currencyCode;
	private GatewayResponse gatewayResponse;
	private Error error;
	private String statusReason;
}
