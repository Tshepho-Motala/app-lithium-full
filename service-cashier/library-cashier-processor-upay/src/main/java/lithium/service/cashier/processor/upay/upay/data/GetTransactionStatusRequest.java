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
public class GetTransactionStatusRequest {

	@JsonProperty("transaction_id")
	private String transactionId;
	
	private String key;
	
	private Long ts;
	
	private String sign;
	
	public GetTransactionStatusRequest sign(String secret) {
		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(key, ts, secret);
		calc.addItem("transaction_id", transactionId);
		this.sign = calc.calculateHash();
		return this;
	}

}
