package lithium.service.cashier.controllers.internal;

import lithium.service.Response;
import lithium.service.cashier.services.transactionbulk.TransactionBulkProcessingService;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/transaction-bulk-processing")
public class InternalTransactionBulkController {

	@Autowired
	private TransactionBulkProcessingService transactionBulkProcessingService;

	@RequestMapping(path = "/proceed-codes", method = RequestMethod.GET)
	public Response<Void> proceedCodes(
			@RequestParam("userGuid") String guid,
			@RequestParam("actions") List<TransactionProcessingCode> actions,
			@RequestParam("comment") String comment,
			LithiumTokenUtil token) {
		{
			for (TransactionProcessingCode action : actions) {
				try {
					transactionBulkProcessingService.proceed(action, guid, comment, token);
				} catch (Exception e) {
					log.error("Failed to move withdrawals to " + action + " for guid=" + guid + " :" + e.getMessage(), e);
					return Response.<Void>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
				}
			}
			return Response.<Void>builder().status(Response.Status.OK).build();
		}
	}
}
