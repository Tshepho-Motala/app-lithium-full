package lithium.service.cashier.processor.wumg.directeller.wsdl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InitDepositResponseDT {
	@JsonProperty("id")
	private String id;
	@JsonProperty("message")
	private String message;
	@JsonProperty("transaction_status")
	private String transactionStatus;
	
	@JsonProperty("status")
	private String status;
	@JsonProperty("datetime")
	private String datetime;
	@JsonProperty("transaction_state")
	private String transactionState;
	@JsonProperty("trans_id")
	private String transactionId;
	@JsonProperty("merchant_code")
	private String merchantCode;
	@JsonProperty("receiver_code")
	private String receiverCode;
	@JsonProperty("receiver_name")
	private String receiverName;
	@JsonProperty("receiver_city")
	private String receiverCity;
	@JsonProperty("receiver_state")
	private String receiverState;
	@JsonProperty("receiver_country")
	private String receiverCountry;
	@JsonProperty("receiver_country_code")
	private String receiverCountryCode;
	@JsonProperty("external_trace_id")
	private String externalTraceId;
}