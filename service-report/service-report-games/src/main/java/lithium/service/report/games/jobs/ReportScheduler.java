package lithium.service.report.games.jobs;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

import lithium.service.report.games.config.ServiceReportGamesConfigurationProperties;
import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.repositories.ReportRepository;
import lithium.service.report.games.data.repositories.ReportRunRepository;
import lithium.service.report.games.services.ReportRunService;
import lithium.service.report.games.services.StringValueService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportScheduler {
	
	@Autowired ReportRepository reportRepository;
	@Autowired ReportRunRepository reportRunRepository;
	@Autowired StringValueService stringValueService;
	@Autowired ReportRunService reportRunService;
	@Autowired ServiceReportGamesConfigurationProperties properties;
	
	@Scheduled(fixedDelay=1000)
	public void run() throws Exception {
		log.trace("Running...");
		
		Date now = new Date();
		
		List<Report> enabledReports = reportRepository.findByEnabled(true);
		for (Report report: enabledReports) {
			try {
				
				if (report.getCurrent() == null) {
					log.warn("Report has no current revision. " + report);
					return;
				}

				if (report.getCurrent().getCron() != null && (report.getScheduledDate() == null)) {
					log.trace("The schedule for the job is old and there is a cron schedule. Calculating next run. " + report);
					if (!CronSequenceGenerator.isValidExpression(report.getCurrent().getCron())) {
						log.warn("Report contains an invalid cron schedule. " + report);
						continue;
					}
					CronSequenceGenerator generator = new CronSequenceGenerator(report.getCurrent().getCron());
					report.setScheduledDate(generator.next(now));
					reportRepository.save(report);
					log.trace("Report next run calculated and saved. " + report);
					continue;
				}
				
				if (report.getScheduledDate() == null || report.getScheduledDate().getTime() > now.getTime()) {
					log.trace("Report is not yet scheduled to run. " + report);
					continue;
				}
				
				if (report.getLastCompleted() != null && report.getLastCompleted().getStartedOn().getTime() >= report.getScheduledDate().getTime()) {
					log.trace("The last completed run is after the scheduled run. Nothing left to do. " + report);
					continue;
				}
				
				if (report.getRunning() != null) {
					Hours hoursBetween = Hours.hoursBetween(new DateTime(report.getRunningSince()), new DateTime(now));
					if (hoursBetween.getHours() >= properties.getReportRunRestart().getRestartAfterHours()) {
						log.error("Report has been running for more than " + properties.getReportRunRestart() + " hours. Setting current report run as failed and"
									+ " resetting schedule to next cron date. " + report);
						ReportRun reportRun = reportRunRepository.findOne(report.getRunning().getId());
						reportRun.setCompleted(true);
						reportRun.setCompletedOn(new Date());
						reportRun.setFailed(true);
						reportRun.setFailReason("Stuck report - running for more than " + properties.getReportRunRestart().getRestartAfterHours() + " hours");
						reportRun = reportRunRepository.save(reportRun);
						report = reportRepository.findOne(report.getId());
						report.setLastFailed(reportRun);
						report.setRunRetriesCount((report.getRunRetriesCount() != null)? (report.getRunRetriesCount() + 1): 1);
						report.setRunning(null);
						Date nextScheduledDate = getNextScheduledDate(now, report);
						if (nextScheduledDate != null) report.setScheduledDate(nextScheduledDate);
						report = reportRepository.save(report);
						continue;
					} else {
						log.trace("Report is already running " + report);
						continue;
					}
				}
				
				if (report.getRunRetriesCount() != null && report.getRunRetriesCount() >= properties.getReportRunRetry().getMaxNumberOfRetries()) {
					log.trace("Report run max number of retries reached. Will calculate and save next report run if report contains a valid cron expression. " + report);
					Date nextScheduledDate = getNextScheduledDate(now, report);
					if (nextScheduledDate != null) report.setScheduledDate(nextScheduledDate);
					report.setRunRetriesCount(null);
					report.setRunning(null);
					reportRepository.save(report);
					continue;
				}
				
				log.info("Running report " + report);
				
				reportRunService.run(report, "scheduler");
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {}
				
			} catch (Exception e) {
				log.error("Unhandled exception during the processing of report " + report, e);
			}
		}
	}
	
	private Date getNextScheduledDate(Date now, Report report) {
		if (report.getCurrent().getCron() != null && CronSequenceGenerator.isValidExpression(report.getCurrent().getCron())) {
			CronSequenceGenerator generator = new CronSequenceGenerator(report.getCurrent().getCron());
			return generator.next(now);
		}
		return null;
	}
}