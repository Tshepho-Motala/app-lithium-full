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
public class InitPayoutResponseDT {
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
	@JsonProperty("sender_name")
	private String senderName;
	@JsonProperty("sender_city")
	private String senderCity;
	@JsonProperty("sender_state")
	private String senderState;
	@JsonProperty("sender_country")
	private String senderCountry;
	@JsonProperty("sender_country_code")
	private String senderCountryCode;
	@JsonProperty("external_trace_id")
	private String externalTraceId;
}