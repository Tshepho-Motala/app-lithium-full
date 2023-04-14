package lithium.service.cashier.controllers.internal;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalRestrictionController {

	@Autowired
	TransactionService transactionService;

	@GetMapping("/restriction/hours-since-transaction")
	public Response<Long> hoursSinceTransaction(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("transactionType") TransactionType transactionType) {
		return Response.<Long>builder().data(transactionService.hoursSinceTransaction(userGuid, transactionType)).status(Response.Status.OK).build();
	}

	@RequestMapping(path="/restriction/first-transaction-date", method= RequestMethod.GET)
	public Response<Date> firstTransactionDate(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("transactionType") TransactionType transactionType) {
		Transaction transaction = transactionService.findFirstTransaction(userGuid, transactionType, DoMachineState.SUCCESS.name());
		return Response.<Date>builder().data(transaction != null ? transaction.getCreatedOn() : null ).status(Response.Status.OK).build();
	}

}
