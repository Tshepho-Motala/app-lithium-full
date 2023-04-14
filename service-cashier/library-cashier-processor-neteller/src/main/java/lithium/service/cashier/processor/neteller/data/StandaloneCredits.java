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
public class StandaloneCredits {
	private String id;
	private String merchantRefNum;
	private String paymentHandleToken;
	private Long amount;
	private String currencyCode;
	private Boolean dupCheck;
	private Date txnTime;
	private String paymentType;
	private Status status;
	private String description;
	private String customerIp;
}
