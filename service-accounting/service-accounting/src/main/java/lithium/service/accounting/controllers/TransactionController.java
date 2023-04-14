package lithium.service.accounting.controllers;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {
	@Autowired AccountingService accountingService;

	@TimeThisMethod
	@RequestMapping("/{transactionId}")
	public ResponseEntity<List<TransactionEntry>> get(
		@PathVariable Long transactionId
	) throws Status510AccountingProviderUnavailableException {
		SW.start("transactions");
		ResponseEntity<List<TransactionEntry>> transactions = accountingService.accountingClient(true).transactions(transactionId);
		SW.stop();
		return transactions;
	}
}
