package lithium.service.stats.client.stream.event;

import lithium.service.stats.client.objects.StatSummaryBatch;

public interface ICompletedStatsProcessor {

  /**
   * Method to be used for processing of completed stats events by the implementing service
   *
   * @param request
   * @throws Exception
   */
  void processCompletedStats(final StatSummaryBatch request) throws Exception;
}
