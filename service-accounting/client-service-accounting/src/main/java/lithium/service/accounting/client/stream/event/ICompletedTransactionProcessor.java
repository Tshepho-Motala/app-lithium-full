package lithium.service.accounting.client.stream.event;

import lithium.service.accounting.objects.CompleteTransaction;

public interface ICompletedTransactionProcessor {

  /**
   * Method to be used for processing of completed accounting events by the implementing service
   *
   * @param request
   * @throws Exception
   */
  void processCompletedTransaction(final CompleteTransaction request) throws Exception;
}
