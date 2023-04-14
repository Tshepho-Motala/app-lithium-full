package lithium.service.cashier.processor.wumg.paycr;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.mg.FieldValidatorMG;
import lithium.service.cashier.method.wu.FieldValidatorWU;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorWUMGAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage2(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = false;
		
		valid |= FieldValidatorMG.validateControlNumber(FieldValidatorMG.CONTROL_NUMBER_FIELD, STAGE_2, request, response);
		
		valid |= FieldValidatorWU.validateControlNumber(FieldValidatorWU.CONTROL_NUMBER_FIELD, STAGE_2, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
