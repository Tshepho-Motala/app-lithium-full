package lithium.service.report.games.controllers;

import java.security.Principal;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.entities.ReportRevision;
import lithium.service.report.games.data.objects.ReportBasic;
import lithium.service.report.games.data.repositories.ReportActionRepository;
import lithium.service.report.games.data.repositories.ReportFilterRepository;
import lithium.service.report.games.data.repositories.ReportRepository;
import lithium.service.report.games.data.repositories.ReportRevisionRepository;
import lithium.service.report.games.services.ReportActionLabelValueService;
import lithium.service.report.games.services.ReportService;

@RestController
@RequestMapping("/report/games")
public class ReportCreateController {
	@Autowired ReportRepository reportRepository;
	@Autowired ReportRevisionRepository reportRevisionRepository;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired ReportService reportService;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	
	@PostMapping
	@Transactional(rollbackOn=Exception.class)
	public Response<Report> create(@RequestBody ReportBasic reportBasic, Principal principal) throws Exception {
		reportService.checkPermission(reportBasic.getDomainName(), principal);
		Report report = Report.builder()
			.domainName(reportBasic.getDomainName())
			.createdBy(principal.getName())
			.createdDate(new Date())
			.enabled(reportBasic.isEnabled())
			.build();
		report = reportRepository.save(report);
		ReportRevision rev = ReportRevision.builder()
			.report(report)
			.name(reportBasic.getName())
			.description(reportBasic.getDescription())
			.granularity(reportBasic.getGameDataPeriod())
			.granularityOffset(reportBasic.getGameDataPeriodOffset() != null? reportBasic.getGameDataPeriodOffset(): 0)
			.compareXperiods(reportBasic.getCompareXperiods() != null? reportBasic.getCompareXperiods(): null)
			.updateBy(principal.getName())
			.allFiltersApplicable(reportBasic.isAllFiltersApplicable())
			.cron(reportBasic.getCron())
			.build();
		rev = reportRevisionRepository.save(rev);
		report.setCurrent(rev);
		reportRepository.save(report);
		reportService.addFilters(rev, reportBasic.getFilters());
		reportService.addActionsAndActionLabelValues(rev, reportBasic.getActions());
		return Response.<Report>builder().data(report).status(Status.OK).build();
	}
}
