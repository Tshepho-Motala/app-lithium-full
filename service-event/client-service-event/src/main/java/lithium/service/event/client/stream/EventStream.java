package lithium.service.event.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.event.client.objects.EventStreamData;

@Service
public class EventStream {
	
	@Autowired
	private EventOutputQueue channel;

	public void register(EventStreamData entry) {
		channel.outputQueue().send(MessageBuilder.<Object>withPayload(entry).build());
	}
}