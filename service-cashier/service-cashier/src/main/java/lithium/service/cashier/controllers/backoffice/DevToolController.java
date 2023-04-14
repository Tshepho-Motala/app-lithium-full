package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.cashier.ServiceCashierApplication;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.enums.AccountType;
import lithium.service.cashier.services.CashierService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/backoffice/dev-tools/{domainName}")
public class DevToolController {

    private final LithiumServiceClientFactory services;
    private final CashierService cashierService;
    private final CachingDomainClientService cachingDomainClient;

    @GetMapping("/empty-out-pending-withdraw-balance")
    public Response emptyOutPendingWithdrawBalance(@PathVariable("domainName") String domainName,  @RequestParam("guid") String guid,
                                                   @RequestParam("updatePlayerBalance") boolean updatePlayerBalance,
                                                   LithiumTokenUtil tokenUtil) throws Exception {

        String currency = cachingDomainClient.getDefaultDomainCurrency(domainName);

        String message = "Empty out negative pending withdrawal balance success";
	    Long playerBalance = cashierService.getCustomerBalance(currency, domainName, guid, "PLAYER_BALANCE", "PLAYER_BALANCE");
        Long pendingWithdrawBalance = cashierService.getCustomerBalance(currency, domainName, guid, "PLAYER_BALANCE_PENDING_WITHDRAWAL", "PLAYER_BALANCE");
        if (pendingWithdrawBalance == 0) {
            message = "Can't do empty out pending withdraw balance due it's zero.";
            log.info(message + " (" + guid + ")");
            return Response.builder().status(Response.Status.OK).data(buildData(playerBalance, pendingWithdrawBalance)).message(message).build();
        }

        String comment = ServiceCashierApplication.COMMENT_LABEL + "=empty out negative pending withdrawal balance";
        String transactionId = ServiceCashierApplication.TRAN_ID_LABEL + "=empty-out-pending-withdraw-balance-" + guid + "-" + System.currentTimeMillis();
        Response<AdjustmentTransaction> adjustmentPendingWithdraw = getAccountingService().adjustMulti(
                pendingWithdrawBalance * -1,
                DateTime.now().toString(),
                CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //accountCode
                AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
                CashierTranType.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //transactionTypeCode
                AccountType.PLAYER_BALANCE.getCode(), //contraAccountCode
                AccountType.PLAYER_BALANCE.getCode(), //contraAccountTypeCode
                new String[]{transactionId, comment},
                currency,
                domainName,
                guid,
                tokenUtil.guid(),
                true,
                new String[]{AccountType.PLAYER_BALANCE.getCode()}
        );

        if (!adjustmentPendingWithdraw.isSuccessful() || adjustmentPendingWithdraw.getData().getStatus() != AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
            message = "A technical error occurred during the empty out pending withdrawal balance. Error: " + adjustmentPendingWithdraw.getMessage();
            log.error(message + " (" + guid + ")");
            return Response.builder().message(message).status(Response.Status.FORBIDDEN).build();
        }

        if (updatePlayerBalance) {
            Response<AdjustmentTransaction> adjustmentPlayerBalance = getAccountingService().adjustMulti(
                    pendingWithdrawBalance * -1,
                    DateTime.now().toString(),
                    AccountType.PLAYER_BALANCE.getCode(), //accountCode
                    AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
                    "BALANCE_ADJUST", //transactionTypeCode
                    "GF_MANUAL_BALANCE_ADJUST", //contraAccountCode
                    AccountType.MANUAL_BALANCE_ADJUST.name(), //contraAccountTypeCode
                    new String[]{ServiceCashierApplication.COMMENT_LABEL + "=adjust funds from player balance after empty out pending withdrawal balance"},
                    currency,
                    domainName,
                    guid,
                    tokenUtil.guid(),
                    true,
                    new String[]{AccountType.PLAYER_BALANCE.getCode()}
            );

            if (!adjustmentPlayerBalance.isSuccessful() || adjustmentPlayerBalance.getData().getStatus() != AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
                message = "A technical error occurred during the manual adjust player balance after empty out pending withdrawal balance.";
                log.error(message + " (" + guid + ")");
                return Response.builder().status(Response.Status.FORBIDDEN).message(message).build();
            } else {
                message += "\nManual adjust player balance success";
            }
        }
        log.info(message + " (" + guid + ")");

	    playerBalance = cashierService.getCustomerBalance(currency, domainName, guid, "PLAYER_BALANCE", "PLAYER_BALANCE");
	    pendingWithdrawBalance = cashierService.getCustomerBalance(currency, domainName, guid, "PLAYER_BALANCE_PENDING_WITHDRAWAL", "PLAYER_BALANCE");

        return Response.builder().status(Response.Status.OK).data(buildData(playerBalance, pendingWithdrawBalance)).message(message).build();
    }

	private Map<String, String> buildData(Long playerBalance, Long pendingWithdrawBalance) {
		Map<String, String> data = new HashMap<>();
		data.put("playerBalance", playerBalance.toString());
		data.put("pendingWithdrawBalance", pendingWithdrawBalance.toString());
		return data;
	}

	private AccountingClient getAccountingService() throws Exception {
        return services.target(AccountingClient.class, "service-accounting", true);
    }

}
