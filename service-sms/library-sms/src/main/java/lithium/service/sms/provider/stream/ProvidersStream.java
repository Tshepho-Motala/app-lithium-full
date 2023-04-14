package lithium.service.sms.provider.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.sms.client.objects.Provider;

@Service
public class ProvidersStream {
	@Autowired
	private ProvidersStreamOutputQueue channel;
	
	public void registerProvider(Provider provider) {
		channel.channel().send(MessageBuilder.<Provider>withPayload(provider).build());
	}
}