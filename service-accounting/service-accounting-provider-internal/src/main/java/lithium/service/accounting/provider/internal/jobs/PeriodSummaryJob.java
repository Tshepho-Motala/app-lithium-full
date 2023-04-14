package lithium.service.accounting.provider.internal.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.accounting.provider.internal.conditional.SummariesConditional;
import lithium.service.accounting.provider.internal.data.entities.SummaryProcessingBoundary;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeGroup;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeLabelValueGroup;
import lithium.service.accounting.provider.internal.data.objects.group.PeriodAccountCodeTransactionTypeGroup;
import lithium.service.accounting.provider.internal.services.SummaryDomainLabelValueService;
import lithium.service.accounting.provider.internal.services.SummaryDomainService;
import lithium.service.accounting.provider.internal.services.SummaryDomainTransactionTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.LinkedHashSet;

@Component
@Slf4j
@Conditional(SummariesConditional.class)
public class PeriodSummaryJob {
	
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired SummaryDomainService summaryDomainService;
	@Autowired SummaryDomainTransactionTypeService summaryDomainTransactionTypeService;
	@Autowired SummaryDomainLabelValueService summaryDomainLabelValueService;

	private boolean shouldHistoricalRun = true;
	public PeriodSummaryJob() {
	}

	@Scheduled(cron="${lithium.service.accounting.provider.internal.jobs.summaries.cron:*/10 * * * * *}")
	public void calculateAccountCodeSummaries() {
		accountCodeSummaryRun(false);

		if (shouldHistoricalRun) {
			accountCodeSummaryRun(true);
		}

	/*
		NOTE: Here was removed legacy code: *aux label summary reverse* feature.
		You can find removed source using git blame on this line
	 */

	}

	private void accountCodeSummaryRun(boolean historical) {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		log.debug("PeriodSummaryJob calculating summaries");

		LinkedHashSet<PeriodAccountCodeGroup> periodAccountCodeSet = new LinkedHashSet<>(1000);
		LinkedHashSet<PeriodAccountCodeTransactionTypeGroup> periodAccountCodeTransactionTypeSet = new LinkedHashSet<>(1000);
		LinkedHashSet<PeriodAccountCodeLabelValueGroup> periodAccountCodeLabelValueSet = new LinkedHashSet<>(1000);

		StopWatch sw = new StopWatch(this.getClass().getSimpleName() + (historical ? "Historical":"Real"));
		SummaryProcessingBoundary summaryProcessingBoundary = null;
		sw.start("getUnsummarizedPeriodSets");
		try {
			summaryProcessingBoundary = summaryDomainService
					.getUnsummarizedPeriodSets(periodAccountCodeSet, periodAccountCodeTransactionTypeSet, periodAccountCodeLabelValueSet, historical);
			if (historical && summaryProcessingBoundary == null) {
				// We can stop running historical portion of the job.
				log.info("historical off");
				shouldHistoricalRun = false;
			}
		} catch (Exception e) {
			log.error("Problem getting unsummarized period sets", e);
		}
		sw.stop();

		sw.start("summaryDomainService");
		summaryDomainService.calculateDamaged(periodAccountCodeSet);
		sw.stop();

		sw.start("summaryDomainTransactionTypeService");
		summaryDomainTransactionTypeService.calculateDamaged(periodAccountCodeTransactionTypeSet);
		sw.stop();

		sw.start("summaryDomainLabelValueService");
		try {
			summaryDomainLabelValueService.calculateDamaged(periodAccountCodeLabelValueSet);
		} catch (Exception e) {
			log.error("Problem in summary label value execution.", e);
			if (sw.getTotalTimeMillis() > 5000) {
				log.error(sw.toString());
			} else	if (sw.getTotalTimeMillis() > 1000) {
				log.warn(sw.toString());
			} else {
				log.debug(sw.toString());
			}
			return;
		}
		sw.stop();

		sw.start("endUnsummarizedPeriod_"+ (summaryProcessingBoundary == null ? "none" : summaryProcessingBoundary.getLastTransactionIdProcessed() + "_" + summaryProcessingBoundary.getLastTransactionLabelValueIdProcessed()));
		summaryDomainService.endUnsummarizedPeriod(summaryProcessingBoundary, historical);
		sw.stop();

		if (sw.getTotalTimeMillis() > 5000) {
			log.error(sw.toString());
		} else	if (sw.getTotalTimeMillis() > 1000) {
			log.warn(sw.toString());
		} else {
			log.debug(sw.toString());
		}
	}
}
