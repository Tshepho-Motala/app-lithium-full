package lithium.service.cashier.processor.cc.qwipi.data;

import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ValidationException extends Exception {
	
	public ValidationException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.description(), cause);
		this.errorCode = errorCode;
	}
	
	public ValidationException(ErrorCode errorCode) {
		super(errorCode.description());
		this.errorCode = errorCode;
	}
	
	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;
}
