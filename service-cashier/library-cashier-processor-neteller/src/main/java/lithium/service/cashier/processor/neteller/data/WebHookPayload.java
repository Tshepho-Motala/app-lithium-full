package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.PaymentType;
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
public class WebHookPayload {
	private String accountId;
	private String id;
	private String merchantRefNum;
	private Long amount;
	private String currencyCode;
	private Status status;
	private PaymentType paymentType;
	private Date txnTime;
}
