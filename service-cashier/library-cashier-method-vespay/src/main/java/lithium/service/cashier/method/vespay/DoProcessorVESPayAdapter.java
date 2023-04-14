package lithium.service.cashier.method.vespay;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorVESPayAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage2(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorVESPay.validateVoucherCode(FieldValidatorVESPay.VOUCHER_CODE, STAGE_2, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
