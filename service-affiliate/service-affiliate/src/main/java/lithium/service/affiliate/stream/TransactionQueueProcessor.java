package lithium.service.affiliate.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.affiliate.service.TransactionEnrichmentService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(TransactionQueueSink.class)
@Slf4j
public class TransactionQueueProcessor {
	
	@Autowired TransactionEnrichmentService tranEnrichmentService;
	
	//FIXME: If error occurs, place tran back on queue or do some magic to keep it on queue until processed (saw something in queue docs, just need to go read about it again).
	@StreamListener(TransactionQueueSink.INPUT) 
	void handle(TransactionStreamData entry) throws Exception {
		log.info("Received a transaction from the queue for processing: " + entry);
		
		tranEnrichmentService.enrichAccountingTransactionIfAffiliatedPlayer(entry.getTransactionId(), entry.getOwnerGuid(), entry.getTransactionType());
		
		log.info("Completed a transaction from the queue for processing: " + entry);
	}
	
}
