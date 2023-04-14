package lithium.service.accounting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient("service-accounting")
public interface AccountingTransactionClient {
	@RequestMapping("/transaction/find-external-transaction-id")
	public Long findExternalTransactionId(@RequestParam("externalTransactionId") String externalTransactionId, @RequestParam("transactionTypeCode") String transactionTypeCode);

	@RequestMapping("/transaction/find-external-reverse-transaction-id")
	public Long findExternalReverseTransactionId(@RequestParam("externalTransactionId") String externalTransactionId);

	@RequestMapping("/transaction/is-used-free-bet")
	public Boolean isUsedFreeBet(
			@RequestParam("guid") String guid,
			@RequestParam("currency") String currencyCode,
			@RequestParam("accountCode") String accountCodeName,
			@RequestParam("accountType") String accountCodeTypeName
	);
}
