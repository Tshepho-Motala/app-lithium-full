package lithium.service.user.search.services.user_search;

import java.util.function.Predicate;
import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.user.search.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionCompletedEventService implements ICompletedTransactionProcessor {

  @Autowired
  @Qualifier("user_search.CurrentAccountBalanceService")
  private CurrentAccountBalanceService currentAccountBalanceService;
  @Autowired
  @Qualifier("user_search.UserService")
  private UserService userService;


  private final static String BALANCE_CODE = "PLAYER_BALANCE";
  private static final Predicate<TransactionEntry> isPlayerBalance = transactionEntry -> transactionEntry.getAccount().getAccountType().getCode().equals(BALANCE_CODE) && transactionEntry.getAccount().getAccountCode().getCode().equals(BALANCE_CODE);

  @Override
  public void processCompletedTransaction(CompleteTransaction request) {
    log.info("CompleteTransaction: " + request);
    request.getTransactionEntryList()
        .stream()
        .filter(isPlayerBalance)
        .forEach(this::updateBalance);
  }

  private void updateBalance(TransactionEntry transactionEntry){
    Account userAccount = transactionEntry.getAccount();
    User user = userService.findOrCreateUser(userAccount.getOwner().getGuid());
    currentAccountBalanceService.updateLockBalance(user.getGuid(), userAccount.getBalanceCents(), transactionEntry.getDate());
  }
}
