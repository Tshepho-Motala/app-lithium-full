package lithium.service.cashier.method.wu;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorWUAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage2(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorWU.validateControlNumber(FieldValidatorWU.CONTROL_NUMBER_FIELD, STAGE_2, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
