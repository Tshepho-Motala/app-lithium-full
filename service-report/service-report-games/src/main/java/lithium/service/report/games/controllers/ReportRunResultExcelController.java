package lithium.service.report.games.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.repositories.ReportRunRepository;
import lithium.service.report.games.services.ReportRunResultExcelService;

@RestController
@RequestMapping("/report/games/{reportId}/runs/{reportRunId}/xls")
public class ReportRunResultExcelController {
	@Autowired ReportRunRepository repoRun;
	@Autowired ReportRunResultExcelService reportRunResultExcelService;
	
	@GetMapping @ResponseBody
	public void xls(@PathVariable("reportRunId") Long reportRunId, @RequestParam("accessKey") String accessKey, HttpServletResponse response) throws Exception {
		ReportRun run = repoRun.findOne(reportRunId);
		
		if (!run.getAccessKey().equals(accessKey)) throw new AccessDeniedException("Invalid access key");
		
		String fileName = run.getReportRevision().getName() + ".xlsx";
		String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		reportRunResultExcelService.xls(run, response.getOutputStream());
	}
}