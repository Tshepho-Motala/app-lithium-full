package lithium.service.cashier.method.cc;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.enums.CardType;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

public class FieldValidatorCC extends FieldValidator {
	//field names should match what is in json file
	public static final String CVV_FIELD = "cvv";
	public static final String NAME_ON_CARD_FIELD = "nameoncard";
	public static final String CC_NUMBER_FIELD = "ccnumber";
	public static final String EXP_MONTH_FIELD = "expmonth";
	public static final String EXP_YEAR_FIELD = "expyear";
	public static final String CSID_FIELD = "csid";
	public static final String AMOUNT_FIELD = "amount";
	
	public static boolean validateCVV(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(CCValidationType.CVV_LENGTH, key, stage, request, response);
	}
	
	public static boolean validateCardNumber(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
//		return validate(CCValidationType.CARDNUM_LENGTH, key, stage, request, response);
		try {
			List<String> allowedCardTypes = Arrays.asList(request.getProperties().getOrDefault("cardType", "ALL").split(","));
			String ccnumber = request.stageInputData(1, "ccnumber");
			CardType cardType = CardType.detect(ccnumber);
			
			if (cardType == CardType.UNKNOWN) response.stageOutputData(stage).put(FieldValidatorCC.CC_NUMBER_FIELD, "Not a valid card number"); //should possibly use language keys

			if (allowedCardTypes.contains(cardType.shortCode())) return true;
			if (allowedCardTypes.contains("ALL")) return true;
			if (allowedCardTypes.contains("ANY")) return true;

			response.stageOutputData(stage).put(FieldValidatorCC.CC_NUMBER_FIELD, "Card type not allowed."); //should possibly use language keys
			return false;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean validateExpiryMonth(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(CCValidationType.EXP_MONTH_LENGTH, key, stage, request, response);
	}
	
	public static boolean validateExpiryYear(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
		return validate(CCValidationType.EXP_YEAR_LENGTH, key, stage, request, response);
	}

	public enum CCValidationType implements IValidationType {
		CVV_LENGTH(Pattern.compile("^[0-9]{3,4}$"), " is not 3-4 characters."),
		CARDNUM_LENGTH(Pattern.compile("^[0-9]{16,16}$"), " is not 16 digits."),
		EXP_MONTH_LENGTH(Pattern.compile("^(0?[1-9]|1[012])$"), " is not in the range 1-12."),
		EXP_YEAR_LENGTH(Pattern.compile("^[0-9]{4}$"), " does not contain a valid year.");
		
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
