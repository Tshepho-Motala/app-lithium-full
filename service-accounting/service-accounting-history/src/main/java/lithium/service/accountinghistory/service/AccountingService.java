package lithium.service.accountinghistory.service;

import lithium.report.XlsReport;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingClientWithExceptions;
import lithium.service.accounting.client.AccountingFrontendClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainTransactionTypeClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.client.BackofficeBalanceMovementTransactionsClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AccountingService {
	@Autowired AccountingService self;
	@Autowired Environment environment;
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	
	public AccountingClient accountingClient(String url) throws Status510AccountingProviderUnavailableException {
		try {
			return lithiumServiceClientFactory.target(AccountingClient.class, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingClientWithExceptions accountingClientWithExceptions(String url) throws Status510AccountingProviderUnavailableException {
		try {
			return lithiumServiceClientFactory.target(AccountingClientWithExceptions.class, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingClient accountingClient(boolean readOnly) throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = self.provider(null, readOnly).getUrl();
			log.debug("Accounting call using : "+providerUrl);
			return lithiumServiceClientFactory.target(AccountingClient.class, providerUrl, true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingFrontendClient accountingFrontendClient() throws Exception {
		String providerUrl = self.provider(null, true).getUrl();
		log.debug("Accounting Frontend call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingFrontendClient.class, providerUrl,true);
	}

	public AccountingSummaryDomainTransactionTypeClient summaryDomainTransactionTypeClient() throws Exception {
		String providerUrl = self.provider(null, true).getUrl();
		log.debug("Accounting Summary Domain TransactionType call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryDomainTransactionTypeClient.class, providerUrl,true);
	}

	public AccountingSummaryTransactionTypeClient summaryTransactionTypeClient() throws Exception {
		String providerUrl = self.provider(null, true).getUrl();
		log.debug("Accounting Summary TransactionType call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryTransactionTypeClient.class, providerUrl,true);
	}

	public AccountingSummaryDomainLabelValueClient summaryDomainLabelValueClient() throws Exception {
		String providerUrl = self.provider(null, true).getUrl();
		log.debug("Accounting Summary Domain LabelValue call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryDomainLabelValueClient.class, providerUrl,true);
	}

	public AdminTransactionsClient adminTransactionsClient() throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = self.provider(null, true).getUrl();
			log.debug("Admin transactions call using : "+providerUrl);
			return lithiumServiceClientFactory.target(AdminTransactionsClient.class, providerUrl,true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public BackofficeBalanceMovementTransactionsClient backofficeBalanceMovementTransactionsClient() throws Exception {
		String providerUrl = self.provider(null, true).getUrl();
		log.debug("Accounting Summary TransactionType call using : " + providerUrl);
		return lithiumServiceClientFactory.target(BackofficeBalanceMovementTransactionsClient.class, providerUrl, true);
	}
	
	@Cacheable(value = "lithium.service.accounting.service.Provider")
	public Provider provider(String domainName, boolean checkReadOnly) throws Exception {
		log.warn("####################### NOT FROM CACHE #######################");
		Provider provider = null;
		if (domainName != null) {
			log.info("provider(" + domainName + ")");
			ProviderClient providerClient = lithiumServiceClientFactory.target(ProviderClient.class, true);
			log.debug("ProviderClient :" + providerClient);
			Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, ProviderType.ACCOUNTING.type());
			log.debug("Response :" + response);
			Iterable<Provider> list = response.getData();
			log.debug("List :" + list);
			Map<Integer, Provider> map = StreamSupport.stream(list.spliterator(), true)
				.filter(p -> {
					if (p.getProviderType().getName().equals(ProviderType.ACCOUNTING.name())) {
						if (p.getEnabled()) return true;
					}
					return false;
				})
				.sorted((p1, p2) -> p1.getPriority().compareTo(p2.getPriority()))
				.collect(
					Collectors.toMap(
						Provider::getPriority,
						Function.identity()
					)
				);
			log.debug("Map :" + map);
			provider = map.get(1);
			log.debug("Return :" + provider);
		}
		if (provider == null) {
			Boolean readOnlyEnabled = environment.getProperty("lithium.enable-read-only", Boolean.class, false);
			if ((checkReadOnly) && (readOnlyEnabled)) {
				provider = Provider.builder()
					.url("service-accounting-provider-readonly")
					.build();
			} else {
				provider = Provider.builder()
					.url("service-accounting-provider-internal")
					.build();
			}
		}
		return provider;
	}

    public void xls(ArrayList<TransactionEntryBO> transactions, ServletOutputStream outputStream) throws Exception {
        log.debug("Generating excel for "+transactions.size()+" trans.");
        XlsReport report = new XlsReport("name");
        report.run(outputStream, () -> {
            transactionSheet(transactions, report);
        });
    }
    private void transactionSheet(List<TransactionEntryBO> transactions, XlsReport report) {
        NumberFormat amountFormatter = new DecimalFormat("#0.00");
        report.sheet("Balance Movement", Arrays.asList(1,2), () -> {
            report.columnHeading("Date");
            report.columnHeading("Transaction ID");
            report.columnHeading("Transaction Type");
            report.columnHeading("Amount");
            report.columnHeading("Account Balance");
            report.columnHeading("Transaction Currency");
            report.columnHeading("Provider Transaction ID");
        }, () -> {
            for (TransactionEntryBO t:transactions) {
                report.row(() -> {
                    //"Date"
                    report.cellDateTime(t.getTransaction().getCreatedOn());
                    //"Transaction ID"
                    report.cellNumeric(t.getTransaction().getId());
                    //"Transaction Type"
                    report.cell(t.getTransaction().getTransactionType().getCode());
                    //"Amount"
                    report.cell(amountFormatter.format(t.getAmountCents() / 100d));
                    //"Balance"
                    report.cell(amountFormatter.format(t.getPostEntryAccountBalanceCents()/ 100d));
                    //"Transaction Currency"
                    report.cell(t.getAccount().getCurrency().getCode());
                    //"Provider Transaction ID"
                    report.cell(t.getDetails().getExternalTranId());
                });
            }
        });
    }
}
