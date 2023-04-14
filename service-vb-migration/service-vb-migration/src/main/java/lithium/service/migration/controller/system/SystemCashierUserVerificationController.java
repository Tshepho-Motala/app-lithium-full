package lithium.service.migration.controller.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.migration.service.cashier.CashierUserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/cashier-user-verification")

public class SystemCashierUserVerificationController {
  private final CashierUserVerificationService service;

  @Autowired
  public SystemCashierUserVerificationController(CashierUserVerificationService service) {
    this.service = service;
  }

  @GetMapping("/has-user-made-cashier-transactions")
  public boolean hasUserMadeCashierTransactions(@RequestParam("customerId") String customerId)
      throws Status500InternalServerErrorException {
    return service.hasUserMadeCashierTransactions(customerId);
  }

  @GetMapping("/has-user-made-cashier-transactions-using-provider")
  public boolean hasUserMadeCashierTransactions(@RequestParam("customerId") String customerId, @RequestParam("providerName") String providerName)
      throws Status500InternalServerErrorException {
    return service.hasUserMadeCashierTransactionsUsingProvider(customerId, providerName);
  }
}
