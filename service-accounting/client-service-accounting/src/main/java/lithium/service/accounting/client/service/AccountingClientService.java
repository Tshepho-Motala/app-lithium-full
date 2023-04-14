package lithium.service.accounting.client.service;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status425DateParseException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingClientWithExceptions;
import lithium.service.accounting.client.AccountingPlayerLimitSystemClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.client.AccountingTransactionClient;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AccountCode;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.SummaryAccount;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AccountingClientService implements AccountingClientWithExceptions, AdminTransactionsClient {
    @Autowired Environment environment;
    @Autowired LithiumServiceClientFactory services;

    public static final String ACCOUNT_CODE_PLAYER_BALANCE_PENDING_WITHDRAWAL = "PLAYER_BALANCE_PENDING_WITHDRAWAL";
    public static final String ACCOUNT_CODE_CASHIER_PAYOUT = "CASHIER_PAYOUT";
    public static final String TRAN_TYPE_WITHDRAWAL = "CASHIER_PAYOUT";
	public static final String ACCOUNT_CODE_PLAYER_CASHIER_DEPOSIT = "CASHIER_DEPOSIT";
	public static final String TRAN_TYPE_DEPOSIT = "CASHIER_DEPOSIT";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * This method is just here to keep mvend working. The correct place to get this from is casino
     * There still needs to be a client created from casino for the balance methods with business logic for calculation
     */
    @Deprecated
    public Response<Long> getBalance(String currencyCode, String domainName, String ownerGuid) throws Exception {
        return services.target(AccountingClient.class, "service-accounting", true)
                .get(currencyCode, domainName, ownerGuid);
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public Response<Map<String, Long>> getBalanceMapByAccountType(
            @RequestParam("domainName") String domainName,
            @RequestParam("accountType") String accountType,
            @RequestParam("currencyCode") String currencyCode,
            @RequestParam("ownerGuid") String ownerGuid
    ) throws
            Status510AccountingProviderUnavailableException,
            Status412AccountingDomainNotFoundException,
            Status410AccountingAccountTypeNotFoundException,
            Status411AccountingUserNotFoundException,
            Status413AccountingCurrencyNotFoundException {
        return accountingClientWithExceptions()
                .getBalanceMapByAccountType(domainName, accountType, currencyCode, ownerGuid);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AdjustmentResponse adjust(AdjustmentRequest request)
            throws Status500InternalServerErrorException,
            Status510AccountingProviderUnavailableException,
            Status414AccountingTransactionDataValidationException,
            Status415NegativeBalanceException {
        return accountingClientWithExceptions().adjust(request);
    }

    public Response<AdjustmentTransaction> adjustMulti(
        Long amountCents,
        String date,
        String accountCode,
        String accountTypeCode,
        String transactionTypeCode,
        String contraAccountCode,
        String contraAccountTypeCode,
        String[] labels,
        String currencyCode,
        String domainName,
        String ownerGuid,
        String authorGuid,
        Boolean allowNegativeAdjust,
        String[] negAdjProbeAccCodes
    ) throws Exception {
        return accountingClient().adjustMulti(
            amountCents,
            date,
            accountCode,
            accountTypeCode,
            transactionTypeCode,
            contraAccountCode,
            contraAccountTypeCode,
            labels,
            currencyCode,
            domainName,
            ownerGuid,
            authorGuid,
            allowNegativeAdjust,
            negAdjProbeAccCodes
        );
    }

    public Response<AdjustmentTransaction> adjustMultiV2(
            AdjustMultiRequest request
    ) throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException {
        return accountingClient().adjustMultiV2(request);
    }

    @Override
    public DataTableResponse<TransactionEntryBO> table(
        String dateRangeStart, String dateRangeEnd, String userGuid, String transactionId, String draw, int start,
        int length, String searchValue,  String providerGuid, String providerTransId, List<String> transactionType,
        String additionalReference, String orderDirection, String lboAccessToken, String domainName, String accountCode, String roundId
    ) throws Status425DateParseException, Status510AccountingProviderUnavailableException {
        return adminTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid, transactionId, draw, start, length,
                searchValue, providerGuid, providerTransId, transactionType, additionalReference,
                orderDirection, lboAccessToken, domainName, accountCode, roundId);
    }

    public List<TransactionEntry> transactions(Long transactionId) throws Exception {
        ResponseEntity<List<TransactionEntry>> transactions = accountingClient().transactions(transactionId);
        if (transactions.getStatusCode().is2xxSuccessful()) return transactions.getBody();
        return null;
    }

    /**
     * Utility method to get the accounting client with exceptions and wrap the exception in a coded exception
     * @return {@link AccountingClientWithExceptions}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AccountingClientWithExceptions accountingClientWithExceptions() throws Status510AccountingProviderUnavailableException {
        try {
            return services.target(AccountingClientWithExceptions.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    /**
     * Utility method to get the accounting client with exceptions and wrap the exception in a coded exception
     * @return {@link AccountingClient}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AccountingClient accountingClient() throws Status510AccountingProviderUnavailableException {
        try {
            return services.target(AccountingClient.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    /**
     * Utility method to get the accounting client with exceptions and wrap the exception in a coded exception
     * @return {@link AccountingSummaryAccountClient}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AccountingSummaryAccountClient accountingSummaryAccountClient() throws Status510AccountingProviderUnavailableException {
        try {
            return services.target(AccountingSummaryAccountClient.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    /**
     * Utility method to get the accounting client with exceptions and wrap the exception in a coded exception
     * @return {@link AccountingTransactionClient}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AccountingTransactionClient accountingTransactionClient(boolean checkReadOnly) throws Status510AccountingProviderUnavailableException {
        try {
            Boolean readOnlyEnabled = environment.getProperty("lithium.enable-read-only", Boolean.class, false);
            String provider = checkReadOnly && readOnlyEnabled ? "service-accounting-provider-readonly" : "service-accounting-provider-internal";
            return services.target(AccountingTransactionClient.class, provider, true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    /**
     * Utility method to get the transactions client with exceptions and wrap the exception in a coded exception
     * @return {@link AdminTransactionsClient}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AdminTransactionsClient adminTransactionsClient() throws Status510AccountingProviderUnavailableException {
        try {
            return services.target(AdminTransactionsClient.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    /**
     * Utility method to get the accounting summery transaction type with exceptions and wrap the exception in a coded exception
     * @return {@link AccountingSummaryTransactionTypeClient}
     * @throws Status510AccountingProviderUnavailableException
     */
    private AccountingSummaryTransactionTypeClient accountingSummaryTransactionTypeClient() throws Status510AccountingProviderUnavailableException {
        try {
            return services.target(AccountingSummaryTransactionTypeClient.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    @Override
    public Response<List<TransactionType>> getAllTransactionTypes() throws Status510AccountingProviderUnavailableException {
        return adminTransactionsClient().getAllTransactionTypes();
    }

    @Override
    public Response<List<AccountCode>> getAllAccountCodes() throws Status510AccountingProviderUnavailableException {
        return adminTransactionsClient().getAllAccountCodes();
    }

    public Long findExternalTransactionId(String externalTransactionId, String transactionTypeCode) throws Status510AccountingProviderUnavailableException {
        return accountingTransactionClient(true).findExternalTransactionId(externalTransactionId, transactionTypeCode);
    }

    public Long findExternalReverseTransactionId(String externalTransactionId) throws Status510AccountingProviderUnavailableException {
        return accountingTransactionClient(true).findExternalReverseTransactionId(externalTransactionId);
    }

    public Long findLifetimeWithdrawalsAmountInCentsByUserAndGranularityAndCurrency(String domain, String ownerGuid, Integer granularity, String currency) throws Exception {
        Response<List<SummaryAccountTransactionType>> response = accountingSummaryTransactionTypeClient().findByUser(
                domain, ownerGuid, granularity, ACCOUNT_CODE_PLAYER_BALANCE_PENDING_WITHDRAWAL, TRAN_TYPE_WITHDRAWAL, currency
        );
        return response.getData().stream()
                .findFirst()
                .map(SummaryAccountTransactionType::getBalance)
                .orElse(0l);
    }

    public Long findLifetimeDepositAmountInCentsByUserAndGranularityAndCurrency(String domain, String ownerGuid, Integer granularity, String currency) throws Exception {
        Response<List<SummaryAccountTransactionType>> response = accountingSummaryTransactionTypeClient().findByUser(
            domain, ownerGuid, granularity, ACCOUNT_CODE_PLAYER_CASHIER_DEPOSIT, TRAN_TYPE_DEPOSIT, currency
        );
        return response.getData().stream()
                .findFirst()
                .map(SummaryAccountTransactionType::getBalance)
                .orElse(0l);
    }

    public Long findLastAmountInCentsByUserAndAccountCodeAndTransactionTypeAndGranularityAndCurrency(String domain, String ownerGuid, String accountCode, String transactionType, Integer granularity, String currency) throws Exception {
        Response<List<SummaryTransactionType>> response = accountingSummaryTransactionTypeClient().findLastByOwnerGuid(
            domain, ownerGuid, 0, granularity, accountCode, transactionType, currency
        );
        return response.getData().stream()
            .findFirst()
            .map(stt -> ((stt.getDebitCents() - stt.getCreditCents())*-1))
            .orElse(0l);
    }

    public Long findPendingWithdrawalsAmountInCentsByUserAndGranularityAndCurrency(String domain, String ownerGuid, Integer granularity, String currency) throws Exception {
        Response<List<SummaryAccount>> response = accountingSummaryAccountClient().findByUser(
                domain, ownerGuid, granularity, ACCOUNT_CODE_PLAYER_BALANCE_PENDING_WITHDRAWAL, currency
        );
        SummaryAccount userSummaryAccount = response.getData().get(0);
        return (userSummaryAccount.getDebitCents() - userSummaryAccount.getCreditCents()) * -1;
    }

    public Long setBalanceLimit(String domainName, String playerGuid, Long amountCents, String currencyCode, String accountCode,
                                String accountTypeCode, String transactionTypeCode, String contraAccountCode, String contraAccountTypeCode) throws Status510AccountingProviderUnavailableException {
        try {

            AccountingPlayerLimitSystemClient client = services.target(AccountingPlayerLimitSystemClient.class, "service-accounting-provider-internal", true);
            Response<Long> response = client.setLimit(domainName, playerGuid, amountCents, currencyCode, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode);

            return response.getData();

        } catch (LithiumServiceClientFactoryException e) {
            throw new Status510AccountingProviderUnavailableException(e.getMessage());
        }
    }

    public Boolean isUsedFreeBet(String guid, String currency, String accountCode, String accountType) throws Status510AccountingProviderUnavailableException {
        return accountingTransactionClient(true).isUsedFreeBet(guid, currency, accountCode, accountType);
    }

	public Long getUserTurnoverFrom(String guid, Date lastDepositDate, List<String> accCodes, String granularity) throws Status510AccountingProviderUnavailableException {
		String strDate = DATE_FORMAT.format(lastDepositDate);
		return accountingSummaryAccountClient().getUserTurnoverFrom(guid, strDate, accCodes, granularity);
	}
}
