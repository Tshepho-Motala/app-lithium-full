package lithium.service.affiliate.controllers;

import java.security.Principal;
import java.util.Date;

import javax.transaction.Transactional;

import lithium.service.affiliate.data.entities.Report;
import lithium.service.affiliate.data.entities.ReportRevision;
import lithium.service.affiliate.data.objects.ReportBasic;
import lithium.service.affiliate.data.repositories.ReportActionRepository;
import lithium.service.affiliate.data.repositories.ReportFilterRepository;
import lithium.service.affiliate.data.repositories.ReportRepository;
import lithium.service.affiliate.data.repositories.ReportRevisionRepository;
import lithium.service.affiliate.services.ReportService;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.services.ReportActionLabelValueService;

@RestController
@RequestMapping("/report/ia")
public class ReportCreateController {
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	ReportRevisionRepository reportRevisionRepository;
	@Autowired
	ReportFilterRepository reportFilterRepository;
	@Autowired
	ReportActionRepository reportActionRepository;
	@Autowired
	ReportService reportService;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	
	@PostMapping
	@Transactional(rollbackOn=Exception.class)
	public Response<Report> create(@RequestBody ReportBasic reportBasic, Principal principal) throws Exception {
		//reportService.checkPermission(reportBasic.getDomainName(), principal);
		Date scheduledDate = null;
		if (reportBasic.getChosenDate() != null && reportBasic.getChosenTime() != null) {
			DateTime date = DateTime.parse(reportBasic.getChosenDate(), ISODateTimeFormat.dateTimeParser());
			DateTime time = DateTime.parse(reportBasic.getChosenTime(), ISODateTimeFormat.dateTimeParser());
			date = date.withHourOfDay(time.getHourOfDay());
			date = date.withMinuteOfHour(time.getMinuteOfHour());
			date = date.withSecondOfMinute(0);
			scheduledDate = date.toDate();
		}
		if (scheduledDate == null && reportBasic.getCron() == null) scheduledDate = new Date();
		Report report = Report.builder()
			.domainName(reportBasic.getDomainName())
			.createdBy(principal.getName())
			.createdDate(new Date())
			.enabled(reportBasic.isEnabled())
			.scheduledDate(scheduledDate)
			.build();
		report = reportRepository.save(report);
		ReportRevision rev = ReportRevision.builder()
			.report(report)
			.name(reportBasic.getName())
			.description(reportBasic.getDescription())
			.granularity(reportBasic.getPlayerDataPeriod())
			.granularityOffset(reportBasic.getPlayerDataPeriodOffset() != null? reportBasic.getPlayerDataPeriodOffset(): 0)
			.updateBy(principal.getName())
			.allFiltersApplicable(reportBasic.isAllFiltersApplicable())
			.cron((reportBasic.getCron() != null)? reportBasic.getCron(): null)
			.chosenDateString((reportBasic.getChosenDate() != null)? reportBasic.getChosenDate(): null)
			.chosenTimeString((reportBasic.getChosenTime() != null)? reportBasic.getChosenTime(): null)
			.ggrPercentageDeduction(reportBasic.getGgrPercentageDeduction())
			.build();
		rev = reportRevisionRepository.save(rev);
		report.setCurrent(rev);
		reportRepository.save(report);
		reportService.addFilters(rev, reportBasic.getFilters());
		reportService.addActionsAndActionLabelValues(rev, reportBasic.getActions());
		return Response.<Report>builder().data(report).status(Status.OK).build();
	}
}
