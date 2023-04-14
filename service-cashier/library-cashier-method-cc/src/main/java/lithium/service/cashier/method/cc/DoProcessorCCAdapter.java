package lithium.service.cashier.method.cc;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorCCAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorCC.validateCVV(FieldValidatorCC.CVV_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateCardNumber(FieldValidatorCC.CC_NUMBER_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateExpiryMonth(FieldValidatorCC.EXP_MONTH_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateExpiryYear(FieldValidatorCC.EXP_YEAR_FIELD, STAGE_1, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
