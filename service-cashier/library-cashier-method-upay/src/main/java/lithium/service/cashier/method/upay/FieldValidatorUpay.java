package lithium.service.cashier.method.upay;

import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

public class FieldValidatorUpay extends FieldValidator {
	//field names should match what is in json file
	public static final String AMOUNT_FIELD = "amount";
	
//	public static boolean validateControlNumber(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
//		return validate(MGValidationType.CONTROL_NUMBER_FORMAT, key, stage, request, response);
//	}
//
//	public enum MGValidationType implements IValidationType {
//		CONTROL_NUMBER_FORMAT(Pattern.compile("^[0-9]{8}$"), " is not 8 digits.");
//		
//		MGValidationType(Pattern regex, String error) {
//			this.regex = regex;
//			this.error = error;
//			
//		}
//		
//		@Getter
//		private Pattern regex;
//		
//		@Getter
//		private String error;
//	}
}
