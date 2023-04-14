package lithium.service.geo.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.geo.client.objects.GeoLabelTransactionStreamData;

@Service
public class GeoStream {
	
	@Autowired
	private GeoOutputQueue channel;

	public void register(GeoLabelTransactionStreamData entry) {
		channel.outputQueue().send(MessageBuilder.<Object>withPayload(entry).build());
	}
}