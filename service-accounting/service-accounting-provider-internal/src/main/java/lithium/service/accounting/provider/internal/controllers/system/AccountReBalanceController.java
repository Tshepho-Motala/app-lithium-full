package lithium.service.accounting.provider.internal.controllers.system;

import java.util.List;
import lithium.service.Response;
import lithium.service.accounting.provider.internal.data.objects.group.AccountReBalanceRequest;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountReBalance;
import lithium.service.accounting.provider.internal.services.AccountsReBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/accounts")
public class AccountReBalanceController {

    @Autowired
    private AccountsReBalanceService accountsReBalanceService;

    @PostMapping("/rebalance")
    public Response<List<SummaryAccountReBalance>> reBalanceAccounts(@RequestBody AccountReBalanceRequest accountReBalanceRequest) {
        List<SummaryAccountReBalance> accountReBalances =
                accountsReBalanceService.reBalanceOperation(accountReBalanceRequest);
        return Response.<List<SummaryAccountReBalance>>builder().data(accountReBalances).status(Response.Status.OK_SUCCESS).build();
    }
}
