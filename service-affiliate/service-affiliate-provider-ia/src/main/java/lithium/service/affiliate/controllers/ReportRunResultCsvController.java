package lithium.service.affiliate.controllers;

import lithium.service.affiliate.ServiceAffiliatePrvIncomeAccessModuleInfo;
import lithium.service.affiliate.data.entities.ReportRun;
import lithium.service.affiliate.data.repositories.ReportRunRepository;
import lithium.service.affiliate.services.ReportActionService;
import lithium.service.affiliate.services.ReportRunResultCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/report/ia/{reportId}/runs/{reportRunId}")
public class ReportRunResultCsvController {
	
	@Autowired
	ReportRunRepository repoRun;
	@Autowired
	ReportRunResultCsvService reportRunResultCsvService;
	@Autowired
	ReportActionService repostActionService;
	@Value("${spring.application.name}")
	private String applicationName;

	
	@PostMapping("/csvsales") @ResponseBody
	public void csvsales(@PathVariable("reportRunId") Long reportRunId, @RequestParam("accessKey") String accessKey, HttpServletResponse response) throws Exception {

		ReportRun run = repoRun.findOne(reportRunId);

		if (!run.getAccessKey().equals(accessKey)) throw new AccessDeniedException("Invalid access key");
		final Map<String, String> providerConfigProperties = repostActionService.getProviderConfigProperties(applicationName, run.getReportRevision().getReport().getDomainName());
		String merchantName = providerConfigProperties.get(ServiceAffiliatePrvIncomeAccessModuleInfo.ConfigProperties.SFTP_USERNAME.getValue());
		Date periodStartDate = run.getPeriodStartDate();
		if (periodStartDate == null) periodStartDate = run.getStartedOn();
		String fileName =  merchantName + "_SALES_" + new SimpleDateFormat("yyyyMMdd").format(periodStartDate) + ".csv";
		String mimeType = "text/csv";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		reportRunResultCsvService.csvSales(run, response.getOutputStream());

	}

	@PostMapping("/csvreg") @ResponseBody
	public void csvreg(@PathVariable("reportRunId") Long reportRunId, @RequestParam("accessKey") String accessKey, HttpServletResponse response) throws Exception {
		ReportRun run = repoRun.findOne(reportRunId);

		if (!run.getAccessKey().equals(accessKey)) throw new AccessDeniedException("Invalid access key");
		final Map<String, String> providerConfigProperties = repostActionService.getProviderConfigProperties(applicationName, run.getReportRevision().getReport().getDomainName());
		String merchantName = providerConfigProperties.get(ServiceAffiliatePrvIncomeAccessModuleInfo.ConfigProperties.SFTP_USERNAME.getValue());
		Date periodStartDate = run.getPeriodStartDate();
		if (periodStartDate == null) periodStartDate = run.getStartedOn();
		String fileName =  merchantName + "_REG_" + new SimpleDateFormat("yyyyMMdd").format(periodStartDate) + ".csv";
		String mimeType = "text/csv";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		reportRunResultCsvService.csvReg(run, response.getOutputStream());
	}
}
