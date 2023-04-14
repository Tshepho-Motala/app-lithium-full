package lithium.service.cashier.method.ids;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorIdsAdapter extends DoProcessorAdapter {
	public static final String PROPERTY_MERCHANT_ID = "merchantId";
	public static final String PROPERTY_MERCHANT_SUB_ID = "merchantSubId";
	public static final String PROPERTY_MERCHANT_PASSWORD= "merchantPassword";
	public static final String PROPERTY_BASE_URL= "baseUrl";
	public static final String PROPERTY_PROCESSOR_IMPL_NAME = "processorImplName";


	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		//valid &= FieldValidatorIds.validateNumeric(FieldValidatorIds.AMOUNT_FIELD, STAGE_1, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
