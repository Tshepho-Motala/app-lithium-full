package lithium.service.affiliate.provider.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.affiliate.provider.service.PapProcessingService;
import lithium.service.affiliate.provider.stream.objects.PapTransactionStreamData;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(ExportQueueSink.class)
@Slf4j
public class ExportQueueProcessor {
	
	@Autowired PapProcessingService papProcessingService;
	
	@StreamListener(ExportQueueSink.INPUT) 
	void handle(PapTransactionStreamData entry) throws Exception {
		log.info("Received a transaction from the queue for processing: " + entry);
		
		papProcessingService.sendDataToPap(entry);
	}
	
}
