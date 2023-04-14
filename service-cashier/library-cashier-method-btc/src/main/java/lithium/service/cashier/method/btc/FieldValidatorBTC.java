package lithium.service.cashier.method.btc;

import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

public class FieldValidatorBTC extends FieldValidator {
	//field names should match what is in json file
	public static final String ADDRESS_FIELD = "address";
	public static final String AMOUNT_FIELD = "amount";
	
	public static boolean validateAddress(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(BTCValidationType.ADDRESS_FIELD, key, stage, request, response);
	}

	public enum BTCValidationType implements IValidationType {
		ADDRESS_FIELD(Pattern.compile("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$"), " is not a valid bitcoin address.");
		
		BTCValidationType(Pattern regex, String error) {
			this.regex = regex;
			this.error = error;
			
		}
		
		@Getter
		private Pattern regex;
		
		@Getter
		private String error;
	}
}
