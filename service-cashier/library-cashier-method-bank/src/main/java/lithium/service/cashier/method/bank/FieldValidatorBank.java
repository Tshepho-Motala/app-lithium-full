package lithium.service.cashier.method.bank;

import lithium.service.cashier.validator.FieldValidator;

public class FieldValidatorBank extends FieldValidator {
	//field names should match what is in json file
	public static final String ACCOUNT_NUMBER_FIELD = "accountNumber";
	public static final String AMOUNT_FIELD = "amount";
}
