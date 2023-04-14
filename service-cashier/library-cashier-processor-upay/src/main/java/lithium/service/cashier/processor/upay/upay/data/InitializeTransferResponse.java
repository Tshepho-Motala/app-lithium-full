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
public class InitializeTransferResponse {
	
	private String status;
	
	private String code;
	
	private String msg;
	
	private String description;
	
	@JsonProperty("order_id")
	private String orderId;
	
	private String hash;
	
	@JsonProperty("token_number")
	private String tokenNumber;
}
