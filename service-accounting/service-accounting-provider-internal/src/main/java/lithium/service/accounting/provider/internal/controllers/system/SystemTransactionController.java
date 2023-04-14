package lithium.service.accounting.provider.internal.controllers.system;

import lithium.service.accounting.client.SystemTransactionClient;
import lithium.service.accounting.provider.internal.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/transaction")
public class SystemTransactionController implements SystemTransactionClient {
	@Autowired private TransactionService service;

	@Override
	@GetMapping("/find-transaction-entries")
	public List<lithium.service.accounting.objects.TransactionEntry> findTransactionEntries(
			@RequestParam("externalTransactionId") String externalTransactionId,
			@RequestParam("transactionTypeCode") String transactionTypeCode) {
		return service.findTransactionEntriesByExternalTransactionId(externalTransactionId, transactionTypeCode);
	}
}
