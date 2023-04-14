package lithium.service.promo.pr.user.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lithium.service.client.objects.Granularity;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.promo.pr.user.dto.Activity;
import lithium.service.promo.pr.user.dto.Category;
import lithium.service.stats.client.objects.DomainStatSummary;
import lithium.service.stats.client.objects.LabelValue;
import lithium.service.stats.client.objects.StatSummary;
import lithium.service.stats.client.objects.StatSummaryBatch;
import lithium.service.stats.client.stream.event.ICompletedStatsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatsCompletedEventService implements ICompletedStatsProcessor {

  @Autowired
  private ProcessSummaryStatsService processSummaryStatsService;

  @Override
  public void processCompletedStats(StatSummaryBatch statSummaryBatch) throws Exception {
    log.debug("Received:: " + statSummaryBatch);
    if (statSummaryBatch.getStatSummaries() != null) {
      if (statSummaryBatch.getEventName().equalsIgnoreCase(Activity.LOGIN.getActivity())) {
        for (StatSummary ss: statSummaryBatch.getStatSummaries()) {
          processSummaryStatsService.processLoginStats(ss);
        }
      } else if (statSummaryBatch.getEventName().equalsIgnoreCase(Activity.REGISTRATION.getActivity())) {
        processSummaryStatsService.processRegistrationStat(statSummaryBatch.getStatSummaries().get(0));
      }
    } else if (statSummaryBatch.getDomainStatSummaries() != null) {
      for (DomainStatSummary ss: statSummaryBatch.getDomainStatSummaries()) {
        log.trace("DomainStatSummary : " + ss);
        log.debug("Domain : " + ss.getDomainStat().getDomain().getName());
        log.debug("Event : " + ss.getDomainStat().getEvent().getName());
        log.debug("Granularity : " + Granularity.fromGranularity(ss.getPeriod().getGranularity()));
        log.debug("Count : " + ss.getCount());
        log.debug("================================================");
      }
    }
  }
}