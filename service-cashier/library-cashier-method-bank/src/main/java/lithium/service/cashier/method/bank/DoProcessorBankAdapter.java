package lithium.service.cashier.method.bank;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;

public class DoProcessorBankAdapter extends DoProcessorAdapter {
	@Override
	protected DoProcessorResponseStatus validateWithdrawalStage1(DoProcessorRequest request, DoProcessorResponse response) {
		return DoProcessorResponseStatus.SUCCESS;
	}
}
