package lithium.server.oauth2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lithium.exceptions.ErrorCodeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = CustomOAuthExceptionSerializer.class)
public class CustomOAuthException extends OAuth2Exception {

	private String errorCode;
	private ErrorCodeException ec;

	public CustomOAuthException(String errorCode, ErrorCodeException ec) {
		super(ec.getMessage());
		this.errorCode = errorCode;
		this.ec = ec;
	}

	@Override
	public String getOAuth2ErrorCode() {
		return errorCode;
	}

	@Override
	public int getHttpErrorCode() { return ec.getCode(); }
}
