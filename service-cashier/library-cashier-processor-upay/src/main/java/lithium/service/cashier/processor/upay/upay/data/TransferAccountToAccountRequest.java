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
public class TransferAccountToAccountRequest {

	@JsonProperty("sender_account")
	String senderAccount;

	@JsonProperty("receiver_account")
	String receiverAccount;
	
	String amount;
	
	String currency;
	
	String test;
	
	String key;
	
	Long ts;
	
	String sign;

	public TransferAccountToAccountRequest sign(String secret) {
		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(key, ts, secret);
		calc.addItem("sender_account", senderAccount);
		calc.addItem("receiver_account", receiverAccount);
		calc.addItem("amount", amount);
		calc.addItem("currency", currency);
		calc.addItem("test", test);
		this.sign = calc.calculateHash();
		return this;
	}
}
