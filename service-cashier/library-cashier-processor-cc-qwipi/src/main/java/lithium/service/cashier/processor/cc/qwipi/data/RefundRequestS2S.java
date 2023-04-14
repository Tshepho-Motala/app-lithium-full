package lithium.service.cashier.processor.cc.qwipi.data;

import java.net.MalformedURLException;
import java.net.URL;

import lithium.service.cashier.processor.cc.qwipi.data.enums.AgreeRiskType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.OperationType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.RefundType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResponseType;
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
public class RefundRequestS2S {
	
	private String resType = ResponseType.JSON.toString();
	private String refundType = RefundType.REGULAR.getCode();
	private String agreeRisk = AgreeRiskType.NO.getCode();
	private String operation = OperationType.REFUND.getCodeString();
	private String orderId;
	private String billNo;
	private String amount;
	private String amountRefund;
	private String returnUrl;
	private String md5Info;
	
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(orderId).addItem(billNo).addItem(amount).addItem(amountRefund);
		return calculator.calculateHash();
	}
	
	public RefundRequestS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}
	
	public void validate(String md5Key) throws ValidationException {
		
		if ((resType != null) && (ResponseType.valueOf(resType) != ResponseType.JSON))  throw new ValidationException(ErrorCode.I0000002);
		if ((refundType != null) && (RefundType.fromCode(refundType) == null)) throw new ValidationException(ErrorCode.I0000003);
		if ((agreeRisk != null) && (AgreeRiskType.fromCode(agreeRisk) == null)) throw new ValidationException(ErrorCode.I0000004);
		if ((operation == null) || (OperationType.fromCodeString(operation) != OperationType.REFUND)) throw new ValidationException(ErrorCode.E1002110);
		if ((orderId == null) || (orderId.isEmpty())) throw new ValidationException(ErrorCode.I0000005);
		if ((amount == null) || (amount.isEmpty())) throw new ValidationException(ErrorCode.E1000320);
		if ((amountRefund == null) || (amountRefund.isEmpty())) throw new ValidationException(ErrorCode.E2000200);
		if ((billNo == null) || (billNo.isEmpty())) throw new ValidationException(ErrorCode.E1000240);
		if ((returnUrl == null) || (returnUrl.isEmpty())) throw new ValidationException(ErrorCode.I0000008);

		if (orderId.length() > 20) throw new ValidationException(ErrorCode.I0000006);
		if (billNo.length() > 40) throw new ValidationException(ErrorCode.E1000250);
		if (amount.length() > 20) throw new ValidationException(ErrorCode.E1001750);
		if (amountRefund.length() > 20) throw new ValidationException(ErrorCode.I0000007);
		if (returnUrl.length() > 100) throw new ValidationException(ErrorCode.I0000009);
		try { new URL(returnUrl); } catch (MalformedURLException mfue) { throw new ValidationException(ErrorCode.I0000010); };
		
		double amountDouble = 0;
		try { amountDouble = Double.parseDouble(amount); } catch (Exception ade) { throw new ValidationException(ErrorCode.E1000340); }
		double amountRefundDouble = 0;
		try { amountRefundDouble = Double.parseDouble(amountRefund); } catch (Exception ade) { throw new ValidationException(ErrorCode.E2000200); }
		
		if (amountDouble < 0) throw new ValidationException(ErrorCode.E1000330);
		if (amountRefundDouble < 0) throw new ValidationException(ErrorCode.E2000200);
		if (amountRefundDouble > amountDouble) throw new ValidationException(ErrorCode.E2000220);
		
		if ((md5Info == null) || (md5Info.isEmpty())) throw new ValidationException(ErrorCode.E1000170);
		if (md5Info.length() > 32) throw new ValidationException(ErrorCode.E1000180);
		if (!md5Info.equals(calculateMd5Info(md5Key))) throw new ValidationException(ErrorCode.E1000190);
	}

}
