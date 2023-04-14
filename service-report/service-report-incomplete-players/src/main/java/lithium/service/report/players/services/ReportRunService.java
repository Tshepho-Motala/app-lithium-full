package lithium.service.report.players.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.report.players.data.entities.Report;
import lithium.service.report.players.data.entities.ReportFilter;
import lithium.service.report.players.data.entities.ReportRevision;
import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.entities.ReportRunResults;
import lithium.service.report.players.data.repositories.ReportFilterRepository;
import lithium.service.report.players.data.repositories.ReportRepository;
import lithium.service.report.players.data.repositories.ReportRunRepository;
import lithium.service.report.players.data.repositories.ReportRunResultsRepository;
import lithium.service.user.client.IncompleteUserClient;
import lithium.service.user.client.objects.IncompleteUser;
import lithium.service.user.client.objects.IncompleteUserFilter;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReportRunService {

	@Autowired ReportRepository reportRepository;
	@Autowired ReportRunRepository reportRunRepository;
	@Autowired ReportRunResultsRepository reportRunResultsRepository;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionService reportActionService;
	@Autowired StringValueService svs;
	@Autowired CachingDomainClientService currencyService;
	
	@Autowired LithiumServiceClientFactory services;
	
	private void run(ReportRun reportRun) throws Exception {

		IncompleteUserClient incompleteUserClient = services.target(IncompleteUserClient.class);

		ReportRevision rev = reportRun.getReportRevision();
		List<ReportFilter> reportFilters = reportFilterRepository.findByReportRevision(rev);

		boolean matchAllFilters = (rev.getAllFiltersApplicable() != null)? rev.getAllFiltersApplicable(): false;
		List<IncompleteUserFilter> filters = new ArrayList<>();
		for (ReportFilter rp: reportFilters) {
			filters.add(IncompleteUserFilter.builder().field(rp.getField()).operator(rp.getOperator()).value(rp.getValue()).build());
		}

		boolean more = true;
		Long count = 0L;
		Long pos = 0L;

		while (more) {
			DataTableResponse<IncompleteUser> response = incompleteUserClient.tableForIncompleteUserReport(reportRun.getReport().getDomainName(), matchAllFilters, JsonStringify.listToString(filters), "1", pos, 10L);
			reportRun.setTotalRecords(response.getRecordsTotal());
			reportRun = reportRunRepository.save(reportRun);
			
			count = count + response.getData().size();
			
			pos = pos + response.getData().size();
			if (count >= response.getRecordsTotal()) more = false;
			
			userLoop:
			for (IncompleteUser user: response.getData()) {
				log.debug("Got user " + user);
				
				ReportRunResults row = ReportRunResults.builder().reportRun(reportRun).build();

				row.setUserId(user.getId());
				row.setUsername((user.getUsername() != null)? svs.link(user.getUsername().toLowerCase()): null);
				row.setEmail((user.getEmail() != null)? svs.link(user.getEmail()): null);
				row.setFirstName((user.getFirstName() != null)? svs.link(user.getFirstName()): null);
				row.setLastName((user.getLastName() != null)? svs.link(user.getLastName()): null);
				row.setCellphoneNumber((user.getCellphoneNumber() != null)? svs.link(user.getCellphoneNumber()): null);
				row.setGender((user.getGender() != null)? svs.link(user.getGender()): null);
				row.setStage((user.getStage() != null)? svs.link(user.getStage()): null);
				row.setCreatedDate(user.getCreatedDate());

				reportRunResultsRepository.save(row);

				reportRun.setProcessedRecords((reportRun.getProcessedRecords() != null)? reportRun.getProcessedRecords() + 1: 1);
				reportRun.setFilteredRecords((reportRun.getFilteredRecords() != null)? reportRun.getFilteredRecords() + 1: 1);
				reportRun = reportRunRepository.save(reportRun);
			}
		}
		
		Thread.sleep(1000);
	}
	
	@Async
	public void run(Report report, String startedBy) {
		
		try {
		
			if (report.getRunning() != null) {
				log.debug("Report " + report + " already running");
				return;
			}
			
			ReportRun reportRun = ReportRun.builder()
			.reportRevision(report.getCurrent())
			.report(report)
			.startedBy(startedBy)
			.startedOn(new Date())
			.build();
			reportRun = reportRunRepository.save(reportRun);
			report.setRunning(reportRun);
			report = reportRepository.save(report);
			
			log.info("Report run started: " + reportRun);
			
			try {
				run(reportRun);

				// It might be some time after we got the last object, and we allow updates of the main report object
				// while reports run, so lets get the latest.
				report = reportRepository.findOne(report.getId());
				reportRun = reportRunRepository.findOne(reportRun.getId());
				
				reportActionService.processActions(reportRun);
				reportRun = reportRunRepository.findOne(reportRun.getId());
				
				reportRun.setTotalRecords(reportRun.getTotalRecords() == null? 0: reportRun.getTotalRecords());
				reportRun.setProcessedRecords(reportRun.getProcessedRecords() == null? 0: reportRun.getProcessedRecords());
				reportRun.setFilteredRecords(reportRun.getFilteredRecords() == null? 0: reportRun.getFilteredRecords());
				reportRun.setActionsPerformed(reportRun.getActionsPerformed() == null? 0: reportRun.getActionsPerformed());
				reportRun.setCompleted(true);
				reportRun.setCompletedOn(new Date());
				report.setLastCompleted(reportRun);
				report.setScheduledDate(null);
				reportRunRepository.save(reportRun);
				
				report.setRunRetriesCount(null);
				
				log.info("Report completed: " + reportRun);

			} catch (Exception ex) {
				log.error("Report run failed: " + reportRun + " " + ex, ex);
				reportRun = reportRunRepository.findOne(reportRun.getId());
				reportRun.setCompleted(true);
				reportRun.setCompletedOn(new Date());
				reportRun.setFailed(true);
				reportRun.setFailReason(ex.getMessage());
				report = reportRepository.findOne(report.getId());
				report.setLastFailed(reportRun);
				report.setRunRetriesCount((report.getRunRetriesCount() != null)? (report.getRunRetriesCount() + 1): 1);
				reportRunRepository.save(reportRun);
			}
			
			report.setRunning(null);
			reportRepository.save(report);
			
		} catch (Exception e) {
			log.error("Unhandled exception runnning report " + report + " " + e, e);
			
		}
		
	}
}
