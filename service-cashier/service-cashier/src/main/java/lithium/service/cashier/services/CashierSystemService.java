package lithium.service.cashier.services;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.domain.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CashierSystemService {
    private final WebApplicationContext beanContext;
    private final TransactionService transactionService;
    private final CashierService cashierService;
    private final UserService userService;

    public Long cancelAllPendingWithdrawals(String domainName, String guid, String comment) throws Exception {

        List<Transaction> pendingWithdrawals = transactionService.findAllTransactions(guid, TransactionType.WITHDRAWAL,
                Arrays.asList(DoMachineState.WAITFORAPPROVAL.toString(),
                        DoMachineState.ON_HOLD.toString(),
                        DoMachineState.AUTO_APPROVED_DELAYED.toString(),
                        DoMachineState.APPROVED_DELAYED.toString()
                ));

        for( Transaction tran : pendingWithdrawals) {
            DoMachine doMachine = beanContext.getBean(DoMachine.class);
            log.info("Trying to cancel pending withdraw transaction id: " + tran.getId() + " user: " + guid);
            doMachine.cancel(tran.getId(), comment);
        }

        Domain domain = userService.retrieveDomainFromDomainService(domainName);

        return cashierService.getCustomerBalance(domain.getCurrency(), domainName, guid);
    }
}
