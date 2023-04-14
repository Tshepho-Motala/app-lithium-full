package lithium.service.accounting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient(name="service-accounting-provider-internal", path="/system/player-balance-limit")
public interface AccountingPlayerLimitSystemClient {

	@RequestMapping("/set-limit")
	Response<Long> setLimit(
			@RequestParam("domainName") String domainName,
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("amountCents") Long amountCents,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("accountTypeCode") String accountTypeCode,
			@RequestParam("transactionTypeCode") String transactionTypeCode,
			@RequestParam("contraAccountCode") String contraAccountCode,
			@RequestParam("contraAccountTypeCode") String contraAccountTypeCode);
}
