package lithium.service.cashier.method.vespay;

import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

public class FieldValidatorVESPay extends FieldValidator {
	public static final String AMOUNT_FIELD = "amount";
	public static final String VOUCHER_CODE = "vouchercode";
	
	public static boolean validateVoucherCode(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(VESPayValidationType.VOUCHER_CODE_NOT_EMPTY, key, stage, request, response);
	}
	
	public enum VESPayValidationType implements IValidationType {
		VOUCHER_CODE_NOT_EMPTY(Pattern.compile("^(?!\\s*$).+"), " is empty.");
		
		VESPayValidationType(Pattern regex, String error) {
			this.regex = regex;
			this.error = error;
			
		}
		
		@Getter
		private Pattern regex;
		
		@Getter
		private String error;
	}
}
