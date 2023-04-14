package lithium.service.cashier.processor.cc.qwipi.data;

import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
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
public class QueryRequestS2S {
	
	private String resType = "JSON";
	private String merNo;
	private String billNo;
	private String md5Info;
	
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(merNo).addItem(billNo);
		return calculator.calculateHash();
	}
	
	public QueryRequestS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}
	
	public void validate(String md5Key) throws ValidationException {
		
		if ((resType != null) && (ResponseType.valueOf(resType) != ResponseType.JSON))  throw new ValidationException(ErrorCode.I0000002);
		if ((merNo == null) || (merNo.isEmpty())) throw new ValidationException(ErrorCode.E1001320);
		if ((billNo == null) || (billNo.isEmpty())) throw new ValidationException(ErrorCode.E1000240);
		if (billNo.length() > 40) throw new ValidationException(ErrorCode.E1000250);
		if ((md5Info == null) || (md5Info.isEmpty())) throw new ValidationException(ErrorCode.E1000170);
		if (md5Info.length() > 32) throw new ValidationException(ErrorCode.E1000180);
		if (!md5Info.equals(calculateMd5Info(md5Key))) throw new ValidationException(ErrorCode.E1000190);
		
//		E1000310(1000310, "Wrong currency"),
//		E1000910(1000910, "State does not meet the country "),

//		private String middleName;
//		private String ssn;
		
		
	}

}
