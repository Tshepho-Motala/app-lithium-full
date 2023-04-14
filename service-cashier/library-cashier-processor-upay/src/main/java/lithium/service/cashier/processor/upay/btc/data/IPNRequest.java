package lithium.service.cashier.processor.upay.btc.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lithium.service.cashier.processor.upay.util.HashCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPNRequest {

	String type;
	String status;
	String order_id;
	String account_id;
	String settled_amount;
	String currency;
	String transaction_id;
	String key;
	Long ts;
	String sign;
	
	public String calculateSign(String secret) {
		HashCalculator calc = new HashCalculator(secret);
		calc.addItem("type", type);
		calc.addItem("status", status);
		calc.addItem("order_id", order_id);
		calc.addItem("account_id", account_id);
		calc.addItem("settled_amount", settled_amount);
		calc.addItem("currency", currency);
		calc.addItem("transaction_id", transaction_id);
		calc.addItem("key", key);
		calc.addItem("ts", ts.toString());
		String hash = calc.calculateHash(true); 
		log.info("Calculating hash using secret: " + secret + " " + this.toString() + " hash " + hash);
		return hash;
	}

	public IPNRequest sign(String secret) {
		this.sign = calculateSign(secret);
		return this;
	}
}
