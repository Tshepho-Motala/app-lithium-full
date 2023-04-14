package lithium.service.cashier.method.btc;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorBTCAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateWithdrawalStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorBTC.validateAddress(FieldValidatorBTC.ADDRESS_FIELD, STAGE_1, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
