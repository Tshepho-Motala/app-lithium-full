package lithium.service.cashier.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.CashierSystemClient;
import lithium.service.cashier.services.CashierSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CashierSystemController implements CashierSystemClient{
    @Autowired
    private CashierSystemService systemService;

    @RequestMapping("/system/cancel-all-pending-withdrawals")
    public Response<Long> cancelAllPendingWithdrawals(@RequestParam("domainName") String domainName, @RequestParam("guid") String guid, @RequestParam("comment") String comment) {
        try {
            log.info("Cancel pending transaction is requested for domain: " + domainName + " guid: " + guid);
            return Response.<Long>builder()
                    .status(Response.Status.OK)
                    .data(systemService.cancelAllPendingWithdrawals(domainName, guid, comment))
                    .build();
        } catch (Exception e) {
            log.error("Failed to cancel pending withdrawals for user: " + guid + " Domain: " + domainName + " Exception: " + e, e);
            return Response.<Long>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build();
        }
    }
}
