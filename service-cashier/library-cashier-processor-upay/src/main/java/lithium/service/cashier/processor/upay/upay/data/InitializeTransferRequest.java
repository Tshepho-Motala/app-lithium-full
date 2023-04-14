package lithium.service.cashier.processor.upay.upay.data;

import java.util.Date;

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
public class InitializeTransferRequest {
	
	@JsonProperty("receiver_account")
	String receiverAccount;
	
	String sender;
	
	String amount;
	
	String currency;
	
	@JsonProperty("order_id")
	String orderId;
	
	String description;
	
	String key;
	
	Long ts;
	
	String sign;

	public InitializeTransferRequest sign(String secret) {
		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(key, ts, secret);
		calc.addItem("receiver_account", receiverAccount);
		calc.addItem("sender", sender);
		calc.addItem("amount", amount);
		calc.addItem("currency", currency);
		calc.addItem("order_id",  orderId);
		calc.addItem("description", description);
		this.sign = calc.calculateHash();
		return this;
	}
}
