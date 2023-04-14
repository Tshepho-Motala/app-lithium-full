package lithium.service.cashier.services.transactionbulk;

import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionBulkProcessingService {
	private Map<TransactionProcessingCode, TransactionBulkProcessor> processorsMap;

	public TransactionBulkProcessingService(List<TransactionBulkProcessor> processorsList) {
		this.processorsMap = processorsList.stream()
				.collect(Collectors.toMap(TransactionBulkProcessor::getCode, Function.identity()));
	}

	public Integer proceed(TransactionProcessingCode code, String guid, String comment, LithiumTokenUtil token) throws Exception {
		return Optional.ofNullable(processorsMap.get(code))
				.map(transactionBulkProcessor -> transactionBulkProcessor.proceed(guid, comment, token))
				.orElseThrow(() -> new Exception("Can't find related processor for " + code));
	}

    public BulkResult proceed(TransactionProcessingCode code, List<Long> transactionIds, String comment, LithiumTokenUtil token) throws Exception {
        return Optional.ofNullable(processorsMap.get(code))
                .map(transactionBulkProcessor -> transactionBulkProcessor.proceed(transactionIds, comment, token))
                .orElseThrow(() -> new Exception("Can't find related processor for " + code));
    }
}
