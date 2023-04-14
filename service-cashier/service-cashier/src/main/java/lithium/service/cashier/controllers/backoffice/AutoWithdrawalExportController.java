package lithium.service.cashier.controllers.backoffice;

import com.google.common.net.HttpHeaders;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.services.AutoWithdrawalRulesetService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/backoffice/auto-withdrawal")
@Slf4j
public class AutoWithdrawalExportController {

	@Autowired
	AutoWithdrawalRulesetService autoWithdrawalService;

	@GetMapping(value = "/export")
	@ResponseBody public void autoWithdrawalDownload(
			@RequestParam("ids") List<Long> autoWithdrawalIds, HttpServletResponse response) throws Exception {
		List<AutoWithdrawalRuleSet> ruleSets = new ArrayList<>();

		autoWithdrawalIds.stream().forEach(id -> {
					AutoWithdrawalRuleSet ruleSet = autoWithdrawalService.findById(id);
					if (ruleSet != null) {
						ruleSet.setId(null);
						ruleSet.setVersion(0);
						ruleSets.add(ruleSet);
						ruleSet.getRules().stream().forEach(autoWithdrawalRule -> {
							autoWithdrawalRule.setId(null);
							autoWithdrawalRule.setVersion(0);
						});
					}
				}
		);

		try {
			String strResponse = autoWithdrawalService.collectRulesToString(ruleSets);

			response.setContentType("application/json");
			response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noStore().getHeaderValue());
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"" + "AutoWithdrawalRules.json" +"\""));
			IOUtils.copy(IOUtils.toInputStream(strResponse), response.getOutputStream());
		} finally {
		response.flushBuffer();
	    }
	}

	@PostMapping(value = "/import")
	public Response<List<AutoWithdrawalRuleSet>> autoWithdrawalUpload(
			@RequestPart("file") final MultipartFile multipartFile
			) {
		try {
			List<AutoWithdrawalRuleSet> data = autoWithdrawalService.importFromFile(multipartFile);
			return Response.<List<AutoWithdrawalRuleSet>>builder().data(data).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Error during import auto-withdrawal from file {}", multipartFile.getOriginalFilename(), e);
			return Response.<List<AutoWithdrawalRuleSet>>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/submit")
	public Response<Void> autoWithdrawalSubmit(
			@RequestBody List<AutoWithdrawalRuleSet> ruleset,
			LithiumTokenUtil tokenUtil
	) {
		for (AutoWithdrawalRuleSet autoWithdrawalRuleSet : ruleset) {
			try {
				autoWithdrawalService.create(
						autoWithdrawalRuleSet.getDomain().getName(),
						autoWithdrawalRuleSet,
						tokenUtil.guid());
			} catch (Status500InternalServerErrorException e) {
				log.error("Failed to create ruleset=[" + autoWithdrawalRuleSet + "]: " + e.getMessage(), e);
				return Response.<Void>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.<Void>builder().status(Response.Status.OK).build();
	}
}
