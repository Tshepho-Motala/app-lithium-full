package lithium.service.cashier.client;

import lithium.service.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-cashier")
public interface CashierSystemClient {
    @RequestMapping("/system/cancel-all-pending-withdrawals")
    public Response<Long> cancelAllPendingWithdrawals(@RequestParam("domainName") String domainName, @RequestParam("guid") String guid, @RequestParam("comment") String comment);
}
