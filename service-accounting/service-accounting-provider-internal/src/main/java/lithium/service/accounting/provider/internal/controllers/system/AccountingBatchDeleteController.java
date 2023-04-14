package lithium.service.accounting.provider.internal.controllers.system;

import lithium.service.accounting.objects.AccountingBatchDeleteRequest;
import lithium.service.accounting.provider.internal.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/transactions")
public class AccountingBatchDeleteController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping("/batch-delete")
    public void findTransactionsBatch(@RequestBody AccountingBatchDeleteRequest batchDeleteRequest) {
        transactionService.findAndDeleteTransactionsBatchById(batchDeleteRequest.getTransactionIds());
    }
}
