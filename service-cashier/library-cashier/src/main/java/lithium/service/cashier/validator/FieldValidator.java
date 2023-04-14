package lithium.service.cashier.validator;

import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lombok.Getter;

public class FieldValidator {
	protected FieldValidator() {};
	
	public static boolean validateNumeric(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(ValidationType.NUMERIC, key, stage, request, response);
	}
	
	protected static boolean validate(IValidationType validationType, final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		try {
			String value = request.stageInputData(stage, key);
			if (validationType.getRegex().matcher(value).matches()) return true;
			
			response.stageOutputData(stage).put(key, key + validationType.getError()); //should possibly use language keys
		} catch (Exception e) {
			response.stageOutputData(stage).put(key, "The "+ key+" field is not present, but it is expected."); //should possibly use language keys
		}
		return false;
	}
	
	public interface IValidationType {
		public Pattern getRegex();
		public String getError();
	}
	
	public enum ValidationType implements IValidationType {
		NUMERIC(Pattern.compile("[0-9]*"), " is not in a number format");
		
		ValidationType(Pattern regex, String error) {
			this.regex = regex;
			this.error = error;
			
		}
		
		@Getter
		private Pattern regex;
		
		@Getter
		private String error;
	}
}
