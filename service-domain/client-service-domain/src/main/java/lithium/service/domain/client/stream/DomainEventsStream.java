package lithium.service.domain.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.DomainEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DomainEventsStream {
	@Autowired
	private DomainEventsOutputQueue channel;
	
	public void process(String domainName, String type, String message, String data) {
		process(
			DomainEvent.builder()
			.domainName(domainName)
			.type(type)
			.message(message)
			.data(data)
			.build()
		);
	}
	
	public void process(DomainEvent domainEvent) {
		try {
			channel.channel().send(MessageBuilder.<DomainEvent>withPayload(domainEvent).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}