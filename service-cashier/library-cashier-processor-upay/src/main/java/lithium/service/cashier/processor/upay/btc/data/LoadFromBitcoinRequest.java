package lithium.service.cashier.processor.upay.btc.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.cashier.processor.upay.util.HashCalculator;
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
public class LoadFromBitcoinRequest {
	
	@JsonProperty("account_id")
	String accountId;
	
	String amount;
	
	String currency;

	@JsonProperty("order_id")
	String orderId;
		
	String key;
	
	Long ts;
	
	String sign;

	public LoadFromBitcoinRequest sign(String secret) {
		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(key, ts, secret);
		calc.addItem("account_id", accountId);
		calc.addItem("amount", amount);
		calc.addItem("currency", currency);
		calc.addItem("order_id", orderId);
		this.sign = calc.calculateHash();
		return this;
	}
}
