package lithium.service.cashier.method.quickbit;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorQuickbitAdapter extends DoProcessorAdapter {
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		return DoProcessorResponseStatus.SUCCESS;
	}
}
