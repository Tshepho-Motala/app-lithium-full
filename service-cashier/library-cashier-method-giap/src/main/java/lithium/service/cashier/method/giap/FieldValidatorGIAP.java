package lithium.service.cashier.method.giap;

import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

public class FieldValidatorGIAP extends FieldValidator {
	//field names should match what is in json file
	public static final String TOKEN_FIELD = "token";
	public static final String PID_FIELD = "productid";
//	public static final String AMOUNT_FIELD = "amount";
	
	//productid
	public static boolean validateTOKEN(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(CCValidationType.TOKEN_LENGTH, key, stage, request, response);
	}
	
	public static boolean validatePID(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(CCValidationType.PID_LENGTH, key, stage, request, response);
	}
	
	public enum CCValidationType implements IValidationType {
		TOKEN_LENGTH(Pattern.compile("^[0-9]{3,4}$"), " is not 3-4 characters."),
		PID_LENGTH(Pattern.compile("^[0-9]{16,16}$"), " is not 16 digits.");
		
		CCValidationType(Pattern regex, String error) {
			this.regex = regex;
			this.error = error;
		}
		
		@Getter
		private Pattern regex;
		
		@Getter
		private String error;
	}
}
