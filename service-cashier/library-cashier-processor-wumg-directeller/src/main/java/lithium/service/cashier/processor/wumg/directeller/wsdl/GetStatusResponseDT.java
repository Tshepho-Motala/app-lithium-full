package lithium.service.cashier.processor.wumg.directeller.wsdl;

import java.math.BigDecimal;

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
public class GetStatusResponseDT {
	@JsonProperty("id")
	private String id;
	@JsonProperty("message")
	private String message;
	@JsonProperty("transaction_status")
	private String transactionStatus;
	
	@JsonProperty("status")
	private String status;
	@JsonProperty("trans_id")
	private String transactionId;
	@JsonProperty("customer_pin")
	private String customerPin;
	@JsonProperty("transaction_state")
	private String transactionState;
	private BigDecimal amount;
	@JsonProperty("amount_indicated")
	private BigDecimal amountIndicated;
	@JsonProperty("currency")
	private String currency;
	@JsonProperty("transfer_fee")
	private BigDecimal transferFee;
	@JsonProperty("dmt_total_fees")
	private BigDecimal dmtTotalFees;
	@JsonProperty("comments")
	private String comments;
	@JsonProperty("trans_control_number")
	private String transactionControlNumber;
	@JsonProperty("sender_beneficiary_info")
	private String senderBeneficiaryInfo;
	@JsonProperty("sender_location")
	private String senderLocation;
	@JsonProperty("external_trace_id")
	private String externalTraceId;
	@JsonProperty("reject_code")
	private String rejectCode;
}