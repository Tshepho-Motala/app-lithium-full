package lithium.service.cashier.processor.cc.trustspay.data;

import javax.xml.bind.annotation.XmlRootElement;

import lithium.service.cashier.processor.cc.trustspay.util.HashCalculator;
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
@XmlRootElement(name="respon")
public class AuthorizeResponse {

	private String merNo;
	private String gatewayNo;
	private String tradeNo;
	private String orderNo;
	private String orderCurrency;
	private String orderAmount;
	private String orderStatus;
	private String orderInfo;
	private String signInfo;
	private String responseCode;
	private String remark;
	
	public String calculateSignInfo(String key) {
		HashCalculator calculator = new HashCalculator(key);
		calculator.addItem(merNo).addItem(gatewayNo).addItem(tradeNo).addItem(orderNo).addItem(orderCurrency).addItem(orderAmount).addItem(orderStatus).addItem(orderInfo);
		return calculator.calculateHash();
	}
	
	public AuthorizeResponse saveSignInfo(String key) {
		this.signInfo = calculateSignInfo(key);
		return this;
	}

}
