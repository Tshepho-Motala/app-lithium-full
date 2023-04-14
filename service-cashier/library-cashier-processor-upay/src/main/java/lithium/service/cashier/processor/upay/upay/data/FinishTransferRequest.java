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
public class FinishTransferRequest {

	@JsonProperty("receiver_account")
	private String receiverAccount;

	private String hash;

	@JsonProperty("token_number")
	private String tokenNumber;
	
	@JsonProperty("token_code")
	private String tokenCode;
	
	private String key;
	
	private Long ts;
	
	private String sign;
	
	public FinishTransferRequest sign(String secret) {
		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(key, ts, secret);
		calc.addItem("receiver_account", receiverAccount);
		calc.addItem("hash", hash);
		calc.addItem("token_number", tokenNumber);
		calc.addItem("token_code", tokenCode);
		this.sign = calc.calculateHash();
		return this;
	}

}
