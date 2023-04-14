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
public class PaymentResponseS2S {

	private String operation;
	private String resultCode;
	private String errorCode;
	private String merNo;
	private String billNo;
	private String amount;
	private String currency;
	private String dateTime;
	private String orderId;
	private String remark;
	private String md5Info;
	private String billingDescriptor;
	
	private String returnUrl;
	private String bgReturnUrl;
		
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(merNo).addItem(billNo).addItem(currency).addItem(amount).addItem(resultCode).addItem(dateTime);
		return calculator.calculateHash();
	}
	
	public PaymentResponseS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}

}
