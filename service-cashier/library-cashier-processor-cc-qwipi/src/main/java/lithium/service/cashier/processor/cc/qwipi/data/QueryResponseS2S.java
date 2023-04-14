package lithium.service.cashier.processor.cc.qwipi.data;

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
public class QueryResponseS2S {

	private String operation;
	private String resultCode;
	private String errorCode;
	private String remark;
	private String merNo;
	private String billNo;
	private String orderId;
	private String status;
	private String statusRemark;
	private String dateOrder;
	private String amount;
	private String currency;
	private String billingDescriptor;
	private String refunded;
	private String dateRefund;
	private String refundRemark;
	private String chargebacked;
	private String dateChargeback;
	private String chargebackRemark;
	private String settled;
	private String dateSettle;
	private String settleRemark;
	private String md5Info;
	
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(merNo).addItem(billNo);
		return calculator.calculateHash();
	}
	
	public QueryResponseS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}

}
