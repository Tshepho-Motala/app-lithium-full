package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingPlayerLimitSystemClient;
import lithium.service.accounting.provider.internal.data.entities.BalanceLimit;
import lithium.service.accounting.provider.internal.services.BalanceLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/player-balance-limit")
public class PlayerBalanceLimitSystemController implements AccountingPlayerLimitSystemClient {
    @Autowired BalanceLimitService balanceLimitService;

    @GetMapping(value = "/set-limit")
    public Response<Long> setLimit(
        @RequestParam("domainName") String domainName,
        @RequestParam("playerGuid") String playerGuid,
        @RequestParam("amountCents") Long amountCents,
        @RequestParam("currencyCode") String currencyCode,
        @RequestParam("accountCode") String accountCode,
        @RequestParam("accountTypeCode") String accountTypeCode,
        @RequestParam("transactionTypeCode") String transactionTypeCode,
        @RequestParam("contraAccountCode") String contraAccountCode,
        @RequestParam("contraAccountTypeCode") String contraAccountTypeCode
    ) {
        log.debug("player-balance-limit set-limit called");
        BalanceLimit balanceLimit = balanceLimitService.createOrUpdate(domainName, playerGuid, amountCents, currencyCode, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode);
        return Response.<Long>builder().status(Response.Status.OK).data(balanceLimit.getId()).build();
    }
}
