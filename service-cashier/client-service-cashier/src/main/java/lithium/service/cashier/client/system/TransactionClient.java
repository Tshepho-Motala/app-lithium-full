package lithium.service.cashier.client.system;

import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.client.datatable.DataTableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-cashier", path = "/system/transaction")
public interface TransactionClient {

    @RequestMapping(value = "/first-deposit", method = RequestMethod.GET)
    CashierClientTransactionDTO findFirstDeposit(@RequestParam("userGuid") String userGuid);
    @RequestMapping(value = "/first-domain", method = RequestMethod.GET)
    void findFirstDomain(@RequestParam("domainName") String domainName);

    @RequestMapping(value = "/last-deposit", method = RequestMethod.GET)
    CashierClientTransactionDTO findLastDeposit(@RequestParam("userGuid") String userGuid);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    DataTableResponse<CashierClientTransactionDTO> searchTransactionsByFilter(
            @RequestBody TransactionFilterRequest filter, @RequestParam Integer page, @RequestParam Integer size
    );

}
