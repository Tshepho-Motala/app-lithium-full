package lithium.service.cashier.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SummaryTransactionTypesService {
    @Autowired
    private LithiumServiceClientFactory services;

    public Long getAccountSummaryByTransactionTypes(String domainName, String userGuid, Integer granularity, String accountCode, List<String> transactionTypes) throws LithiumServiceClientFactoryException {
        Response<List<SummaryAccountTransactionType>> response = getAccountingSummaryTransactionTypeService().findTypesByOwnerGuid(
                domainName,
                userGuid,
                granularity,
                accountCode,
                transactionTypes
        );
        List<SummaryAccountTransactionType> summaries = response.getData();
        return summaries.stream()
                .map(summary -> summary.getCreditCents() - summary.getDebitCents())
                .reduce(Long::sum)
                .orElse(0l);
    }

    private AccountingSummaryTransactionTypeClient getAccountingSummaryTransactionTypeService() throws LithiumServiceClientFactoryException {
        return services.target(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal", true);
    }

}
