package lithium.service.accounting.controllers.system;

import lithium.service.accounting.client.SystemTransactionClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.service.AccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/transaction")
public class SystemTransactionController implements SystemTransactionClient {
	@Autowired private AccountingService service;

	@Override
	@GetMapping("/find-transaction-entries")
	public List<TransactionEntry> findTransactionEntries(
			@RequestParam("externalTransactionId") String externalTransactionId,
			@RequestParam("transactionTypeCode") String transactionTypeCode)
			throws Status510AccountingProviderUnavailableException {
		return service.systemTransactionClient().findTransactionEntries(externalTransactionId, transactionTypeCode);
	}
}
