package lithium.service.accounting.domain.v2.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.domain.v2.services.SummaryReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/summary/reconciliation")
@Slf4j
public class SummaryReconciliationController {
	@Autowired private SummaryReconciliationService service;

	private static final String SHARD_KEY = "SummaryReconciliationAdHoc";

	@PostMapping("/ad-hoc/run")
	public boolean runAdHoc(@RequestParam("dateStr") String date, @RequestParam("dateFormat") String dateFormat)
			throws Status500InternalServerErrorException {
		if ((date == null || date.trim().isEmpty()) || (dateFormat == null || dateFormat.trim().isEmpty())) {
			String msg = "Invalid parameters | date: " + date + ", dateFormat: " + dateFormat;
			log.error("Cannot run summary reconciliation ad-hoc process | " + msg);
			throw new Status500InternalServerErrorException(msg);
		}
		try {
			service.process(SHARD_KEY, date, dateFormat);
			return true;
		} catch (Exception e) {
			log.error("Summary reconciliation ad-hoc process caught an error. Modifications rolled back | {}",
					e.getMessage(), e);
			return false;
		}
	}

	@PostMapping("/process-current-and-total-summary-data")
	public boolean processCurrentAndTotalSummaryData() {
		try {
			service.processCurrentAndTotalSummaryData(SHARD_KEY);
			return true;
		} catch (Exception e) {
			log.error("Summary reconciliation processCurrentAndTotalSummaryData caught an error. Modifications rolled"
					+ " back | {}", e.getMessage(), e);
			return false;
		}
	}
}
