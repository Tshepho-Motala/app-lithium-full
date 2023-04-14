package lithium.service.cashier.processor.upay.btc.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadFromBitcoinResponse {
	
	private String status;
	
	private String msg;
	
	private String description;
	
	@JsonProperty("funds_loads_id")
	private String fundsLoadsId;
	
	@JsonProperty("bitcoin_amount")
	private String bitCoinAmount;
	
	@JsonProperty("crypto_currency_address")
	private String bitCoinAddress;
	
}
