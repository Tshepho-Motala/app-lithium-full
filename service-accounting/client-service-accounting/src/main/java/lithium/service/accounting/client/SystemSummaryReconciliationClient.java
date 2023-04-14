package lithium.service.accounting.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationRequest;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-accounting", path="/system/summary/reconciliation")
public interface SystemSummaryReconciliationClient {
	@RequestMapping(value = "/get-summary-data-for-date", method = RequestMethod.POST)
	public SummaryReconciliationResponse getSummaryDataForDate(@RequestBody SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException, Status510AccountingProviderUnavailableException;

	@RequestMapping(value = "/get-current-and-total-summary-data", method = RequestMethod.POST)
	public SummaryReconciliationResponse getCurrentAndTotalSummaryData(@RequestBody SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException, Status510AccountingProviderUnavailableException;
}
