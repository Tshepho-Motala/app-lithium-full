package lithium.service.user.search.services.cashier;

import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.user.search.data.repositories.cashier.TransactionStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service("cashier.TransactionService")
public class TransactionService {

//  @Autowired
//  @Qualifier("cashier.TransactionRepository")
//  private TransactionRepository transactionRepository;
  @Autowired
  @Qualifier("cashier.TransactionStatusRepository")
  private TransactionStatusRepository transactionStatusRepository;

  public TransactionStatus findStatusByCode(String code) {
    return transactionStatusRepository.findByCode(code);
  }
}
