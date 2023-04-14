package lithium.service.cashier.processor.upay.upay.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetTransactionStatusResponse {

	private String status;
	
	private String msg;
	
	@JsonProperty("transaction_status")
	private String transactionStatus;
	
	@JsonProperty("transaction_code")
	private String transactionCode;
	
	@JsonProperty("transaction_status_desc")
	private String transactionStatusDescription;
	
	private String test;
	
}
