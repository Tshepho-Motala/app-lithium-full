package lithium.service.games.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SupplierGameMetaDataQueueSink {
	String INPUT = "suppliergamemetadatainput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
