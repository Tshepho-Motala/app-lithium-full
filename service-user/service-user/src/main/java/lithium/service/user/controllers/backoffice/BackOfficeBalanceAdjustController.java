package lithium.service.user.controllers.backoffice;

import lithium.client.changelog.ChangeLogService;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.user.data.repositories.TransactionTypeAccountRepository;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}")
public class BackOfficeBalanceAdjustController {
	@Autowired ChangeLogService changeLogService;
	@Autowired MessageSource messageSource;
	@Autowired UserService userService;
	@Autowired TransactionTypeAccountRepository repository;

	@PostMapping("/balance-adjust")
	public Response<AdjustmentTransaction> balanceAdjust(
      @RequestBody AdjustMultiRequest request,
      LithiumTokenUtil token
	) throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, lithium.exceptions.Status500InternalServerErrorException {
		try {
			return userService.adjustBalance(request, token);
		} catch (IllegalArgumentException e) {
			return Response.<AdjustmentTransaction>builder().status(Response.Status.BAD_REQUEST).message(e.getMessage()).build();
		}

	}
	
}
