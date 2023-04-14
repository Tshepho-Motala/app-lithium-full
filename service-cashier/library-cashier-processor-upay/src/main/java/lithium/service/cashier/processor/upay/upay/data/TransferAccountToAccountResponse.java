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
public class TransferAccountToAccountResponse {
	
	private String status;
	
	private String msg;
	
	@JsonProperty("transaction_id")
	private String transactionId;
	
}
