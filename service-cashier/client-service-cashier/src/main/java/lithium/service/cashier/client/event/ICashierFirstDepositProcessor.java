package lithium.service.cashier.client.event;

import lithium.service.cashier.client.objects.SuccessfulTransactionEvent;

public interface ICashierFirstDepositProcessor {

	/**
	 * Method to be used for processing of completed accounting events by the implementing service
	 * @param request
	 * @throws Exception
	 */
	public void processFirstDeposit(final SuccessfulTransactionEvent request) throws Exception;
}
