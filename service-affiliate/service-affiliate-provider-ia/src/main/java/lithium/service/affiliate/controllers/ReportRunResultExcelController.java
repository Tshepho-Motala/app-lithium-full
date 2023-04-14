package lithium.service.affiliate.controllers;

import lithium.service.affiliate.data.entities.ReportRun;
import lithium.service.affiliate.data.repositories.ReportRunRepository;
import lithium.service.affiliate.services.ReportRunResultExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/report/ia/{reportId}/runs/{reportRunId}/xls")
public class ReportRunResultExcelController {
	
	@Autowired
	ReportRunRepository repoRun;
	@Autowired
	ReportRunResultExcelService reportRunResultExcelService;
	
	@PostMapping @ResponseBody
	public void xls(@PathVariable("reportRunId") Long reportRunId, @RequestParam("accessKey") String accessKey, HttpServletResponse response) throws Exception {
		ReportRun run = repoRun.findOne(reportRunId);
		
		if (!run.getAccessKey().equals(accessKey)) throw new AccessDeniedException("Invalid access key");
		
		String fileName = run.getReportRevision().getName() + ".xlsx";
		String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		reportRunResultExcelService.xls(run, response.getOutputStream());
	}
}
