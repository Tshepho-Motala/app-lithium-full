package lithium.service.accounting.provider.internal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lithium.service.accounting.client.AccountingStandardAccountCodes;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.objects.group.AccountReBalanceRequest;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountReBalance;
import lithium.service.accounting.provider.internal.data.repositories.AccountCodeRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainCurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsReBalanceService {

    @Autowired
    private TransactionEntryRepository transactionEntryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private AccountCodeRepository accountCodeRepository;
    @Autowired
    private DomainCurrencyRepository domainCurrencyRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<SummaryAccountReBalance> reBalanceOperation(AccountReBalanceRequest accountReBalanceRequest) {
        Date startDate = accountReBalanceRequest.getDateStart();
        Date endDate = accountReBalanceRequest.getEndDate();
        List<SummaryAccountReBalance> accountReBalances = null;
        if(accountReBalanceRequest.getUserGuid() == null || accountReBalanceRequest.getUserGuid().isEmpty()) {
            Domain domain = domainRepository.findByName(accountReBalanceRequest.getDomainName());
            DomainCurrency domainCurrency = domainCurrencyRepository.findByDomainNameAndCurrencyCode(accountReBalanceRequest.getDomainName(), accountReBalanceRequest.getCurrencyCode());
            if(domainCurrency != null) {
                AccountCode accountType = accountCodeRepository.findByCode(AccountingStandardAccountCodes.PLAYER_BALANCE_ACCOUNT);
                List<Account> accountList = accountRepository.findByDomainAndAccountCodeAndCurrency(domain, accountType, domainCurrency.getCurrency());
                accountReBalances = reBalanceAllAccounts(accountReBalanceRequest.isMock(), accountList, startDate, endDate);
            }
        } else {
            User user = userRepository.findByGuid(accountReBalanceRequest.getUserGuid());
            if(user != null) {
                Domain domain = domainRepository.findByName(accountReBalanceRequest.getDomainName());
                DomainCurrency domainCurrency = domainCurrencyRepository.findByDomainNameAndCurrencyCode(accountReBalanceRequest.getDomainName(), accountReBalanceRequest.getCurrencyCode());
                if(domainCurrency != null) {
                    AccountCode accountCode = accountCodeRepository.findByCode(AccountingStandardAccountCodes.PLAYER_BALANCE_ACCOUNT);
                    List<Account> userAccounts = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountCode(user, domain, domainCurrency.getCurrency() , accountCode);
                    accountReBalances = reBalanceAccount(accountReBalanceRequest.isMock(),userAccounts, startDate, endDate);
                }
            }
        }

        return accountReBalances;
    }

    private List<SummaryAccountReBalance> reBalanceAllAccounts(boolean isMock, List<Account> accounts,Date startDate, Date endDate) {
        List<SummaryAccountReBalance> summaryAccountReBalances = new ArrayList<>();
        for(Account account : accounts) {
            int p = 0;
            Page<TransactionEntry> transactionEntryPage;
            do{
                Pageable pageRequest = PageRequest.of(p, 100);
                transactionEntryPage = transactionEntryRepository.findByAccountAndDateIsBetweenOrderById(account, startDate, endDate, pageRequest);
                summaryAccountReBalance(isMock, summaryAccountReBalances, account, transactionEntryPage);
                p++;
            } while(p < transactionEntryPage.getTotalPages());

        }
        return summaryAccountReBalances;
    }

    private List<SummaryAccountReBalance> reBalanceAccount(boolean isMock, List<Account> userAccounts, Date startDate, Date endDate) {
        List<SummaryAccountReBalance> summaryAccountReBalances = new ArrayList<>();
        for(Account account : userAccounts) {
            int p = 0;
            Page<TransactionEntry> transactionEntryPage;
            do {
                Pageable pageRequest = PageRequest.of(p, 100);
                transactionEntryPage = transactionEntryRepository.findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCodeAndAccountCurrencyCodeOrderById
                        (account.getOwner().getGuid(), startDate, endDate, account.getAccountType().getCode(), account.getCurrency().getCode(), pageRequest);
                summaryAccountReBalance(isMock, summaryAccountReBalances, account, transactionEntryPage);
                p++;
            } while (p < transactionEntryPage.getTotalPages());
        }
        return summaryAccountReBalances;
    }

    private void summaryAccountReBalance(boolean isMock, List<SummaryAccountReBalance> summaryAccountReBalances, Account account, Page<TransactionEntry> transactionEntryPage) {
        Iterator<TransactionEntry> iterator = transactionEntryPage.iterator();
        Iterator<TransactionEntry> secondIterator = transactionEntryPage.iterator();
        List<TransactionEntry> balanceUpdateEntry = new ArrayList<>();
        List<TransactionEntry> transactionEntriesBalanceAdjusted = balances(isMock, iterator, secondIterator, balanceUpdateEntry);
        if(transactionEntriesBalanceAdjusted != null && transactionEntriesBalanceAdjusted.size() > 0) {
            List<lithium.service.accounting.objects.TransactionEntry> transactions = transactionEntriesBalanceAdjusted.stream()
                    .map( s -> modelMapper.map(s, lithium.service.accounting.objects.TransactionEntry.class)).collect(
                            Collectors.toList());
            SummaryAccountReBalance summaryAccountReBalance = SummaryAccountReBalance.builder()
                    .accountId(account.getId())
                    .mismatchedTransactions(transactions.size())
                    .userGuid(account.getOwner().getGuid())
                    .accountCode(account.getAccountCode().getCode())
                    .currencyCode(account.getCurrency().getCode())
                    .domainName(account.getDomain().getName())
                    .build();
            summaryAccountReBalances.add(summaryAccountReBalance);
        }
    }

    private List<TransactionEntry> balances(boolean isMock, Iterator<TransactionEntry> iterator, Iterator<TransactionEntry> secondTransIterator, List<TransactionEntry> balanceUpdateEntry) {
        Iterator<TransactionEntry> firstIterator = iterator;
        Iterator<TransactionEntry> secondIterator = secondTransIterator;

        if(secondIterator.hasNext()) {
            secondIterator.next();
        }

        List<TransactionEntry> accountTransactionList = adjustBalances(firstIterator, secondIterator, balanceUpdateEntry);
        if(!isMock && accountTransactionList != null && accountTransactionList.size() > 0) {
            transactionEntryRepository.saveAll(accountTransactionList);
        }
        return accountTransactionList;
    }

    private List<TransactionEntry> adjustBalances(Iterator<TransactionEntry> firstIterator, Iterator<TransactionEntry> secondIterator, List<TransactionEntry> balanceUpdateEntry) {
        Long accountBalance = 0L;
        List<TransactionEntry> updatedEntries = null;
        outerLoop: while (firstIterator.hasNext()){
            TransactionEntry firstTransactionEntry = firstIterator.next();
            while (secondIterator.hasNext()) {
                TransactionEntry secondTransactionEntry = secondIterator.next();
                updatedEntries = compareBalances(firstTransactionEntry, secondTransactionEntry, accountBalance, balanceUpdateEntry);
                continue outerLoop;
            }
        }
        return updatedEntries;
    }

    private List<TransactionEntry> compareBalances(TransactionEntry previousEntry, TransactionEntry nextEntry, Long accountBalance, List<TransactionEntry> balanceUpdateEntry) {
        final long postEntryAccountBalanceCents = accountBalance != 0L ? accountBalance : previousEntry.getPostEntryAccountBalanceCents();
        final long amountCents = nextEntry.getAmountCents();
        final long balance = postEntryAccountBalanceCents + amountCents;
        if(nextEntry.getPostEntryAccountBalanceCents().longValue() != balance) {
            nextEntry.setPostEntryAccountBalanceCents(balance);
            balanceUpdateEntry.add(nextEntry);
            log.info("[ accountId : " + previousEntry.getAccount().getId() + ", " + "owner : " + previousEntry.getAccount().getOwner().getGuid() + " "
                    + " compared entry : " + previousEntry.getId()
                    + " with : " + nextEntry.getId() + " Performed calculation ( " + postEntryAccountBalanceCents + " + " + nextEntry.getAmountCents() + " = " +  balance + ")");
        }
        return balanceUpdateEntry;
    }
}
