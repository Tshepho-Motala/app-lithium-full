package lithium.service.accounting.client;

import lithium.service.accounting.objects.AccountingBatchDeleteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-accounting-provider-internal")
public interface AccountingBatchTransactionsClient {

    @RequestMapping(value="/system/transactions/batch-delete", method= RequestMethod.POST)
    public void findAndDeleteTransactionsBatch(AccountingBatchDeleteRequest batchDeleteRequest);
}