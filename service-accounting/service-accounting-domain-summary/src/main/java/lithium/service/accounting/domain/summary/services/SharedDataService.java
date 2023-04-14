package lithium.service.accounting.domain.summary.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.client.objects.Granularity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SharedDataService {
    @Autowired private AccountCodeService accountCodeService;
    @Autowired private CurrencyService currencyService;
    @Autowired private DomainService domainService;
    @Autowired private LabelValueService labelValueService;
    @Autowired private PeriodService periodService;
    @Autowired private TransactionTypeService transactionTypeService;

    @TimeThisMethod
    public void findOrCreates(Domain domain, List<CompleteTransaction> transactions) {
        SW.start("shared-data.find-or-creates");
        for (CompleteTransaction transaction: transactions) {
            transactionTypeService.findOrCreate(transaction.getTransactionType());

            for (TransactionLabelBasic tranLabelBasic: transaction.getTransactionLabelList()) {
                if (tranLabelBasic.isSummarize()) {
                    labelValueService.findOrCreate(tranLabelBasic.getLabelName(), tranLabelBasic.getLabelValue());
                }
            }

            for (TransactionEntry entry: transaction.getTransactionEntryList()) {
                Account account = entry.getAccount();
                accountCodeService.findOrCreate(account.getAccountCode().getCode());
                currencyService.findOrCreate(account.getCurrency().getCode(), account.getCurrency().getName());

                Arrays.stream(Granularity.values()).forEach(granularity -> {
                    if (granularity.granularity().intValue() <=
                            Granularity.GRANULARITY_TOTAL.granularity().intValue()) {
                        periodService.findOrCreatePeriod(new DateTime(entry.getDate()), domain,
                                granularity.granularity());
                    }
                });
            }
        }
        SW.stop();
    }
}
