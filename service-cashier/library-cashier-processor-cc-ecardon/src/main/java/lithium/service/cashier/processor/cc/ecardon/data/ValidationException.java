package lithium.service.cashier.processor.cc.ecardon.data;

import java.io.IOException;

import lithium.service.cashier.processor.cc.ecardon.data.enums.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ValidationException extends IOException {
	private static final long serialVersionUID = 1L;
	private ResultCode resultCode;
	
	public ValidationException(ResultCode resultCode, Throwable cause) {
		super(resultCode.getDescription(), cause);
		this.resultCode = resultCode;
	}
	
	public ValidationException(ResultCode resultCode) {
		super(resultCode.getDescription());
		this.resultCode = resultCode;
	}
}