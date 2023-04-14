package lithium.service.domain.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProvidersStream {
	@Autowired
	private ProvidersStreamOutputQueue channel;
	
	public void registerProvider(Integer priority, Boolean enabled, String name, String url, String domainName, String providerTypeName) {
		registerProvider(Provider.builder()
			.priority(priority)
			.enabled(enabled)
			.name(name)
			.url(url)
			.domain(Domain.builder().name(domainName).build())
			.providerType(ProviderType.builder().name(providerTypeName).build())
			.build());
	}

	public void registerProvider(Provider provider) {
		log.info("Registering New Provider : "+provider);
		channel.channel().send(MessageBuilder.<Provider>withPayload(provider).build());
	}
}
