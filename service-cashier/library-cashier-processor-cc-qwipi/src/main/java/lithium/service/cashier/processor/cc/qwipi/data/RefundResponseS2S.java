package lithium.service.cashier.processor.cc.qwipi.data;

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
public class RefundResponseS2S {

	private String operation;
	private String resultCode;
	private String errorCode;
	private String orderId;
	private String billNo;
	private String amount;
	private String amountRefund;
	private String remark;
	private String md5Info;
		
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(orderId).addItem(billNo).addItem(amount).addItem(amountRefund);
		return calculator.calculateHash();
	}
	
	public RefundResponseS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}

}
