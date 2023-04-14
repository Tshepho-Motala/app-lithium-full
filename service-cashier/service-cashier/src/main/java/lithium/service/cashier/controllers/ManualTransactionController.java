package lithium.service.cashier.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.objects.ManualTransaction;
import lithium.service.cashier.data.objects.ManualTransactionFieldValue;
import lithium.service.cashier.exceptions.BalanceGetFailedException;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.CashierService;
import lithium.service.cashier.services.UserService;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.CasinoBonusCheck;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RestController
@RequestMapping("/cashier/manual/transaction")
@Slf4j
public class ManualTransactionController {
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired WebApplicationContext beanContext;
	@Autowired CashierService cashierService;
	@Autowired UserService userService;
	
	@PostMapping
	public Response<Long> addManualTransaction(@RequestBody ManualTransaction manualTransaction, LithiumTokenUtil token) throws Exception {
		log.debug(""+manualTransaction.toString());

		User user = manualTransaction.getUser();
		Domain domain = userService.retrieveDomainFromDomainService(manualTransaction.getUser().getDomain().getName());

		CurrencyAmount amount = CurrencyAmount.fromAmountString(manualTransaction.getAmount());

		TransactionType tranType = (manualTransaction.getTransactionType().equalsIgnoreCase("DEPOSIT"))
								 ? TransactionType.DEPOSIT
								 : TransactionType.WITHDRAWAL;
		
		if (tranType.equals(TransactionType.DEPOSIT)) {
			if (manualTransaction.getBonusId() != null) {
				CasinoBonusClient client = serviceFactory.target(CasinoBonusClient.class);
				Response<Boolean> findBonus = client.checkDepositBonusValidForPlayer(
					CasinoBonusCheck.builder()
					.domainName(domain.getName())
					.bonusId(manualTransaction.getBonusId())
					.playerGuid(user.guid())
					.depositCents(amount.toCents())
					.build()
				);
				if (!findBonus.isSuccessful()) {
					return Response.<Long>builder().data(null).data2(findBonus.getData2().toString()).status(Status.INVALID_DATA).build();
				}
			}
		} else if (tranType.equals(TransactionType.WITHDRAWAL)) {
			long customerBalance = -1;
			try {
				customerBalance = cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(),
					manualTransaction.getUser().guid());
			} catch (BalanceGetFailedException e) {
				throw new Exception("Could not get balance for " + user.guid() + " while trying to add a manual withdrawal transaction");
			}
			if (customerBalance < amount.toCents().longValue()) {
				return Response.<Long>builder().status(Status.INTERNAL_SERVER_ERROR).data2("Player does not have sufficient funds.").build();
			}
		}
		
		DoMachine machine = beanContext.getBean(DoMachine.class);
		DoResponse response = machine.doManualTransaction(user, domain, tranType, amount,
			manualTransaction.getDomainMethodProcessor(), manualTransaction.getFields(),
			manualTransaction.getProcessorReference(), manualTransaction.getBonusId(), token);

		if (response.getError() != null && response.getError())
			return Response.<Long>builder().status(Status.INTERNAL_SERVER_ERROR).data2(response.getErrorMessage()).build();
		else
			return Response.<Long>builder().data(response.getTransactionId()).status(Status.OK).build();
	}
}