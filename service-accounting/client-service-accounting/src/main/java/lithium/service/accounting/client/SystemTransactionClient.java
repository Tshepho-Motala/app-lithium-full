package lithium.service.accounting.client;

import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-accounting", path="/system/transaction")
public interface SystemTransactionClient {
	@RequestMapping(value = "/find-transaction-entries", method = RequestMethod.GET)
	public List<TransactionEntry> findTransactionEntries(
			@RequestParam("externalTransactionId") String externalTransactionId,
			@RequestParam("transactionTypeCode") String transactionTypeCode)
			throws Status510AccountingProviderUnavailableException;
}
