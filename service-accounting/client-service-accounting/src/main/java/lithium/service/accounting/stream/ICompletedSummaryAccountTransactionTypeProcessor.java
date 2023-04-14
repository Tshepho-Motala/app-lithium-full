package lithium.service.accounting.stream;

import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;

public interface ICompletedSummaryAccountTransactionTypeProcessor {

  /**
   * Method to be used for processing of completed SummaryAccountTransactionType events by the implementing service
   *
   * netLossToHouse=null!! net loss to house should rather be calculated on consumption of this event
   *
   * @param request
   * @throws Exception
   */
  void processCompletedSummaryAccountTransactionType(final CompleteSummaryAccountTransactionType request)
  throws Exception;
}
