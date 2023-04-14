package lithium.service.report.players.controllers;

import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.repositories.ReportRunRepository;
import lithium.service.report.players.services.ReportRunResultCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import lithium.service.report.players.services.ReportActionService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/report/players/{reportId}/runs/{reportRunId}/csv")

public class ReportRunResultCsvController {
	
	@Autowired
	ReportRunRepository repoRun;
	@Autowired
	ReportRunResultCsvService reportRunResultCsvService;
	@Autowired
	ReportActionService repostActionService;

	@PostMapping @ResponseBody
	public void csvreg(@PathVariable("reportRunId") Long reportRunId, @RequestParam("accessKey") String accessKey, HttpServletResponse response) throws Exception {

		ReportRun run = repoRun.findOne(reportRunId);

		if (!run.getAccessKey().equals(accessKey)) throw new AccessDeniedException("Invalid access key");


		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateTime = dateFormatter.format(new Date());

		String fileName = run.getReportRevision().getName() + currentDateTime + ".csv";
		String mimeType = "text/csv";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		reportRunResultCsvService.csvReg(run, response.getOutputStream());
	}
}
