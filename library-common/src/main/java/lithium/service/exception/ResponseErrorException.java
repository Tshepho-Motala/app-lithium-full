package lithium.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class ResponseErrorException extends Exception {
	String frontendMessage;
	Integer frontendCode;

	public ResponseErrorException(String frontendMessage) {
		super(frontendMessage);
		this.frontendMessage = frontendMessage;
	}

	public ResponseErrorException(String frontendMessage, String exceptionMessage) {
		super(exceptionMessage);
		this.frontendMessage = frontendMessage;
	}

	public ResponseErrorException(Integer frontendCode, String frontendMessage, String exceptionMessage) {
		super(exceptionMessage);
		this.frontendCode = frontendCode;
		this.frontendMessage = frontendMessage;
	}
}
