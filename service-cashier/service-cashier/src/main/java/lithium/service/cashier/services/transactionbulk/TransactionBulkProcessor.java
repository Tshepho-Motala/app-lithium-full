package lithium.service.cashier.services.transactionbulk;

import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.tokens.LithiumTokenUtil;

import java.util.List;

public interface TransactionBulkProcessor {
	Integer proceed(String guid, String comment, LithiumTokenUtil token);
	TransactionProcessingCode getCode();
    BulkResult proceed(List<Long> transactionIds, String comment, LithiumTokenUtil token);
}
