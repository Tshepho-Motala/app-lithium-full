package lithium.service.cashier.method.giap;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoProcessorGIAPAdapter extends DoProcessorAdapter {
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
//		valid &= FieldValidatorGIAP.validateTOKEN(FieldValidatorGIAP.TOKEN_FIELD, STAGE_1, request, response);
//		valid &= FieldValidatorGIAP.validatePID(FieldValidatorGIAP.PID_FIELD, STAGE_1, request, response);
		
		log.info("DoProcessorGIAPAdapter :: validateDepositStage1");
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
