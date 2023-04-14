package lithium.service.cashier.processor.cc.trustspay.data;

import lithium.service.cashier.processor.cc.trustspay.util.HashCalculator;
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
public class QueryRequest {
	
	private String merNo;
	private String gatewayNo;
	private String orderNo;
	private String signInfo;
	
	public String calculateSignInfo(String key) {
		HashCalculator calculator = new HashCalculator(key);
		calculator.addItem(merNo).addItem(gatewayNo);
		return calculator.calculateHash();
	}
	
	public QueryRequest saveSignInfo(String key) {
		this.signInfo = calculateSignInfo(key);
		return this;
	}
	
	public void validate(String md5Key) throws ValidationException {
	}

}
