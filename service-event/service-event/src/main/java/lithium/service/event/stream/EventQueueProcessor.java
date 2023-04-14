package lithium.service.event.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.event.client.objects.EventStreamData;
import lithium.service.event.service.EventService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(EventQueueSink.class)
@Slf4j
public class EventQueueProcessor {
	
	@Autowired EventService eventService;
	
	//FIXME: If error occurs, place tran back on queue or do some magic to keep it on queue until processed (saw something in queue docs, just need to go read about it again).
	@StreamListener(EventQueueSink.INPUT) 
	void handle(EventStreamData entry) throws Exception {
		log.info("Received an event from the queue for processing: " + entry);
		
		if (eventService.isEventAlreadyHandled(entry)) return;
		
		switch(entry.getEventType()) {
			case EventStreamData.EVENT_TYPE_ZERO_BALANCE: {
				eventService.zeroBalanceEventHandler(entry);
				break;
			}
			default : {
				log.warn("Event received but it is of an unimplemented type: " + entry);
			}
		}
	}
	
}
