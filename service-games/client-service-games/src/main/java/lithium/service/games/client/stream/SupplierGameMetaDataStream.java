package lithium.service.games.client.stream;

import lithium.service.domain.client.objects.DomainEvent;
import lithium.service.domain.client.stream.DomainEventsOutputQueue;
import lithium.service.games.client.objects.supplier.SupplierGameMetaDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SupplierGameMetaDataStream {
	@Autowired
	private SupplierGameMetaDataOutputQueue channel;
	
	public void process(SupplierGameMetaDataMessage message) {
		try {
			channel.channel().send(MessageBuilder.withPayload(message).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}