package lithium.service.user.search.services.user_search;

import java.util.Date;
import lithium.service.user.search.data.entities.CurrentAccountBalance;
import lithium.service.user.search.data.entities.User;
import lithium.service.user.search.data.repositories.user_search.CurrentAccountBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "user_search.CurrentAccountBalanceService")
public class CurrentAccountBalanceService {

  @Autowired
  @Qualifier("user_search.UserService")
  private UserService userService;
  @Autowired
  @Qualifier("user_search.CurrentAccountBalanceRepository")
  private CurrentAccountBalanceRepository currentAccountBalanceRepository;

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
  public void updateLockBalance(String userGuid, long amountCents, Date updateTimestamp) {
    User user = userService.lockingUpdate(userGuid);
    CurrentAccountBalance storedBalance = findOrCreateCurrentAccountBalance(user);
    if (isNeedUpdate(storedBalance.getTimestamp(), updateTimestamp)) {
      storedBalance.setCurrentAccountBalance(amountCents);
      storedBalance.setTimestamp(updateTimestamp);
      currentAccountBalanceRepository.save(storedBalance);
    }
  }

  private CurrentAccountBalance findOrCreateCurrentAccountBalance(User user) {
    CurrentAccountBalance currentAccountBalance = currentAccountBalanceRepository.findByUser(user);
    if (currentAccountBalance == null) {
      CurrentAccountBalance newCurrentAccountBalance = CurrentAccountBalance.builder().user(user).currentAccountBalance(0).build();
      currentAccountBalance = currentAccountBalanceRepository.save(newCurrentAccountBalance);;
      log.info("Created new current account balance row ({}): {}", currentAccountBalance.getId(), currentAccountBalance);
    }
    return currentAccountBalance;
  }

  private boolean isNeedUpdate(Date storedDate, Date updateDate) {
    if (storedDate == null) return true;
    if (updateDate == null) return false;
    return updateDate.after(storedDate);
  }
}
