package lithium.service.cashier.method.cheque;

import lithium.service.cashier.validator.FieldValidator;

public class FieldValidatorCheque extends FieldValidator {
	//field names should match what is in json file
	public static final String ACCOUNT_NUMBER_FIELD = "accountNumber";
	public static final String AMOUNT_FIELD = "amount";
}
