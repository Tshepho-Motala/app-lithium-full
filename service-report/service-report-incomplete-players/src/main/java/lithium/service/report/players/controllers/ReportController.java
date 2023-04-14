package lithium.service.report.players.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.report.players.data.entities.Report;
import lithium.service.report.players.data.entities.ReportAction;
import lithium.service.report.players.data.entities.ReportFilter;
import lithium.service.report.players.data.entities.ReportRevision;
import lithium.service.report.players.data.objects.ReportBasic;
import lithium.service.report.players.data.repositories.ReportActionLabelValueRepository;
import lithium.service.report.players.data.repositories.ReportActionRepository;
import lithium.service.report.players.data.repositories.ReportFilterRepository;
import lithium.service.report.players.data.repositories.ReportRepository;
import lithium.service.report.players.data.repositories.ReportRevisionRepository;
import lithium.service.report.players.services.ReportActionLabelValueService;
import lithium.service.report.players.services.ReportService;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/report/players/{reportId}")
public class ReportController {
	@Autowired ReportRepository reportRepository;
	@Autowired ReportRevisionRepository reportRevisionRepository;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired ReportActionLabelValueRepository reportActionLabelValueRepository;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	@Autowired ReportService reportService;
	
	@GetMapping
	public Response<Report> get(
		@PathVariable("reportId") Report report,
		Principal principal
	) throws Exception {
		reportService.checkPermission(report.getDomainName(), principal);
		return Response.<Report>builder()
				.data(report)
				.status(Status.OK)
				.build();
	}
	
	@GetMapping("/edit")
	@Transactional(rollbackFor=Exception.class)
	public Response<Report> edit(
		@PathVariable("reportId") Report report,
		Principal principal
	) {
		reportService.checkPermission(report.getDomainName(), principal);
		ReportRevision rev = ReportRevision.builder().build();
		if (report.getEdit() == null) {
			reportService.copy(report.getCurrent(), rev, principal.getName());
			rev = reportRevisionRepository.save(rev);
			report.setEdit(rev);
			report = reportRepository.save(report);
			reportService.copyFilters(report.getCurrent(), report.getEdit());
			reportService.copyActionsAndActionLabelValues(report.getCurrent(), report.getEdit());
		}
		return Response.<Report>builder()
				.data(report)
				.status(Status.OK)
				.build();
	}
	
	@PostMapping("/edit/{option}")
	@Transactional(rollbackFor=Exception.class)
	public Response<Report> editPost(
		@PathVariable("reportId") Report report,
		@PathVariable("option") String option,
		@RequestBody ReportBasic editReport,
		Principal principal
	) {
		reportService.checkPermission(report.getDomainName(), principal);
		ReportRevision edit = report.getEdit();
		List<ReportFilter> editFilters = reportFilterRepository.findByReportRevision(edit);
		List<ReportAction> editActions = reportActionRepository.findByReportRevision(edit);
		if (option.equalsIgnoreCase("cancel")) {
			report.setEdit(null);
			report = reportRepository.save(report);
			reportService.deleteFilters(editFilters);
			reportService.deleteActionsAndActionLabelValues(editActions);
			reportRevisionRepository.delete(edit);
			return Response.<Report>builder()
				.data(report).status(Status.OK).build();
		}
		edit.setName(editReport.getName());
		edit.setDescription(editReport.getDescription());
		edit.setAllFiltersApplicable(editReport.isAllFiltersApplicable());
		edit.setCron((editReport.getCron() != null)? editReport.getCron(): null);
		edit.setChosenDateString((editReport.getChosenDate() != null)? editReport.getChosenDate(): null);
		edit.setChosenTimeString((editReport.getChosenTime() != null)? editReport.getChosenTime(): null);
		edit = reportRevisionRepository.save(edit);
		reportService.deleteFilters(editFilters);
		reportService.addFilters(edit, editReport.getFilters());
		reportService.deleteActionsAndActionLabelValues(editActions);
		reportService.addActionsAndActionLabelValues(edit, editReport.getActions());
		if (option.equalsIgnoreCase("commit")) {
			report.setCurrent(edit);
			report.setEdit(null);
			report.setEnabled(editReport.isEnabled());
			Date scheduledDate = null;
			if (edit.getCron() != null) {
				CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(edit.getCron());
				scheduledDate = cronSequenceGenerator.next(new Date());
			} else if (edit.getChosenDateString() != null && edit.getChosenTimeString() != null) {
				DateTime date = DateTime.parse(editReport.getChosenDate(), ISODateTimeFormat.dateTimeParser());
				DateTime time = DateTime.parse(editReport.getChosenTime(), ISODateTimeFormat.dateTimeParser());
				date = date.withHourOfDay(time.getHourOfDay());
				date = date.withMinuteOfHour(time.getMinuteOfHour());
				date = date.withSecondOfMinute(0);
				scheduledDate = date.toDate();
			} else {
				scheduledDate = new Date();
			}
			report.setScheduledDate(scheduledDate);
			report = reportRepository.save(report);
		}
		return Response.<Report>builder()
				.data(report)
				.status(Status.OK)
				.build();
	}
	
	@GetMapping("/filters/{edit}")
	public Response<List<ReportFilter>> getFilters(
		@PathVariable("reportId") Report report,
		@PathVariable("edit") boolean edit,
		Principal principal
	) {
		reportService.checkPermission(report.getDomainName(), principal);
		return Response.<List<ReportFilter>>builder()
				.data(reportFilterRepository.findByReportRevision((edit)? report.getEdit(): report.getCurrent()))
				.status(Status.OK)
				.build();
	}
	
	@GetMapping("/actions/{edit}")
	public Response<List<ReportAction>> getActions(
		@PathVariable("reportId") Report report,
		@PathVariable("edit") boolean edit,
		Principal principal
	) {
		reportService.checkPermission(report.getDomainName(), principal);
		return Response.<List<ReportAction>>builder()
				.data(reportActionRepository.findByReportRevision((edit)? report.getEdit(): report.getCurrent()))
				.status(Status.OK)
				.build();
	}
}