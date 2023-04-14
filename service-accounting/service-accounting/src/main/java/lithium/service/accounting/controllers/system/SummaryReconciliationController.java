package lithium.service.accounting.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.client.SystemSummaryReconciliationClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationRequest;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationResponse;
import lithium.service.accounting.service.AccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/summary/reconciliation")
public class SummaryReconciliationController implements SystemSummaryReconciliationClient {
	@Autowired private AccountingService service;

	@Override
	@PostMapping("/get-summary-data-for-date")
	public SummaryReconciliationResponse getSummaryDataForDate(@RequestBody SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException,  Status510AccountingProviderUnavailableException {
		return service.systemSummaryReconciliationClient().getSummaryDataForDate(request);
	}

	@Override
	@PostMapping("/get-current-and-total-summary-data")
	public SummaryReconciliationResponse getCurrentAndTotalSummaryData(@RequestBody SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException, Status510AccountingProviderUnavailableException {
		return service.systemSummaryReconciliationClient().getCurrentAndTotalSummaryData(request);
	}
}
