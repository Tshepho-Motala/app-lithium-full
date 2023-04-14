package lithium.service.cashier.method.mg;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorMGAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage2(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorMG.validateControlNumber(FieldValidatorMG.CONTROL_NUMBER_FIELD, STAGE_2, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
