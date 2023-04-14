package lithium.service.cashier.controllers.frontend;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.frontend.TransactionFE;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.TransactionService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/frontend/transactions")
public class CashierFrontendTransactionController {
	@Autowired TransactionService transactionService;

	@GetMapping
	@JsonView(Views.Public.class)
	public Response<?> list(
		Locale locale,
		LithiumTokenUtil user
	) throws Status500InternalServerErrorException {
		throw new Status500InternalServerErrorException("This endpoint has been disabled.");
	}

	@RequestMapping("/type")
	public DataTableResponse<TransactionFE> byType(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateEnd,
		@RequestParam("type") String type,
		@RequestParam(value = "status", required = false) String status,
		@RequestParam("pageSize") int pageSize,
		@RequestParam("page") int page,
		Locale locale,
		LithiumTokenUtil user
	) throws Exception { //TODO: Needs to be replaced with ReturnCodeException.
		DateTime dateStartUtc = new DateTime(dateStart.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(), DateTimeZone.UTC); //TODO: Needs to be updated once timezone is stored on domain/user level.
		DateTime dateEndUtc = new DateTime(dateEnd.atTime(23, 59, 59, 999).atZone(ZoneOffset.UTC).toInstant().toEpochMilli(), DateTimeZone.UTC); //TODO: Needs to be updated once timezone is stored on domain/user level.

		return transactionService.findByUserAndType(user.guid(), dateStartUtc, dateEndUtc, type, status, pageSize, page, locale);
	}

	@GetMapping("/table")
	@JsonView(Views.Public.class)
	public DataTableResponse<TransactionFE> table(
		DataTableRequest request,
		Locale locale,
		LithiumTokenUtil user
	) {
		log.trace("transactionService.findByDomainMethodAndOrUser");
		String username = user.username();
		String domainName = user.domainName();
		return new DataTableResponse<>(request, transactionService.findByUser(request, username, domainName, locale));
	}

	@GetMapping("/{transactionId}")
	@JsonView(Views.Public.class)
	public TransactionFE table(
			@PathVariable("transactionId") Transaction transaction,
			Locale locale,
			LithiumTokenUtil user
	) {
		return transaction != null && transaction.getUser().getGuid().equalsIgnoreCase(user.guid()) ? transactionService.toTransactionFE(transaction, locale) : null;
	}
	/**
	 * Cancel a payout/withdrawal request that is in a valid state for cancellation
	 * The response message field will contain information about the boolean outcome if errors occurred
	 * @param user
	 * @param transactionId
	 * @param comment
	 * @return Boolean value with 'true' for cancellation success
	 * @throws Exception
	 */
	@GetMapping("/{transactionId}/cancelPayout")
	public Response<Boolean> cancelPayout(
		LithiumTokenUtil user,
		@PathVariable("transactionId") Long transactionId,
		@RequestParam(name="comment", required=false, defaultValue="Cancel Transaction by player") String comment
	) throws Exception {
		return transactionService.cancelPayoutByPlayer(user, transactionId, comment);
	}
}
