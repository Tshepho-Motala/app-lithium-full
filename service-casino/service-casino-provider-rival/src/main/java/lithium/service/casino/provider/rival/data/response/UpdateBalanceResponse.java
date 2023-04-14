package lithium.service.casino.provider.rival.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString(callSuper=true)
public class UpdateBalanceResponse extends Response {

	@JsonProperty(value="user_error")
	private String userError;
	
	public void setUserError(UserError error) {
		userError = error.getValue();
	}
	
	public enum UserError {
		LIMITS_EXCEEDED("LIMITS_EXCEEDED"),
		INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS");
		
		@Getter
		private String value;
		
		UserError(String errorValue) {
			value = errorValue;
		}
	}
}
