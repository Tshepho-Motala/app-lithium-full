package lithium.service.cashier.processor.btc.clearcollect.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepositResponse {

	@JsonProperty("SUCCESS")
	private String success;
	
	
	@JsonProperty("DEPOSIT")
	private DepositData deposit = new DepositData();
	
	@Data
	public static class DepositData {
		
		@JsonProperty("ID")
		private String id;
		
		@JsonProperty("ADDRESS")
		private String address;
		
		@JsonProperty("REQUESTAMOUNTBTC_INT")
		private String requestAmountBtcSatoshis;
		
	}
}
