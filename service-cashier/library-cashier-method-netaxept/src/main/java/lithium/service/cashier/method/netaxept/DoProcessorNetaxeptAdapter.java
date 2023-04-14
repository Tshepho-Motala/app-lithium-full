package lithium.service.cashier.method.netaxept;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorNetaxeptAdapter extends DoProcessorAdapter {
	public static final String PROPERTY_MERCHANT_ID = "merchantId";
	public static final String PROPERTY_TOKEN = "token";
	public static final String PROPERTY_BASE_URL= "baseUrl";
	public static final String PROPERTY_CALLBACK_URL= "callbackUrl";
	public static final String PROPERTY_WEBSITE_CALLBACK_REDIRECT_URL= "websiteCallbackRedirect";
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		//valid &= FieldValidatorNetaxept.validateNumeric(FieldValidatorNetaxept.AMOUNT_FIELD, STAGE_1, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
}
