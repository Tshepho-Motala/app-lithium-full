package lithium.service.domain.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainProviderLink;
import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderType;
import lithium.service.domain.data.repositories.DomainProviderLinkRepository;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.ProviderRepository;
import lithium.service.domain.data.repositories.ProviderTypeRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(ProviderRegisterQueueSink.class)
public class ProviderRegisterQueueProcessor {
	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private ProviderTypeRepository providerTypeRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private DomainProviderLinkRepository domainProviderLinkRepository;
	
	@StreamListener(ProviderRegisterQueueSink.INPUT) 
	void handle(lithium.service.domain.client.objects.Provider provider) throws Exception {
		log.info("Registering Provider : "+provider);
		ProviderType providerType = providerTypeRepository.findByName(provider.getProviderType().getName());
		if (providerType == null) throw new Exception("Provider Type not found.("+provider.getProviderType().getName()+")");
		Domain domain = domainRepository.findByName(provider.getDomain().getName());
		if (domain == null) throw new Exception("Domain not found.("+provider.getDomain().getName()+")");
		
		// Check for priorities and set accordingly.
		// providerRepository.findByDomainNameAndProviderTypeNameOrderByPriority
		Provider p = providerRepository.findByUrlAndDomainNameAndProviderTypeName(provider.getUrl(), domain.getName(), providerType.getName());
		if (p == null) {
			p = providerRepository.save(Provider.builder()
			.providerType(providerType)
			.name(provider.getName())
			.domain(domain)
			.priority(provider.getPriority())
			.url(provider.getUrl())
			.enabled(provider.getEnabled())
			.build());
		}
		
		createDomainProviderLink(domain, p, true);
	}
	
	private DomainProviderLink createDomainProviderLink(Domain domain, Provider provider, boolean ownerLink) {
		DomainProviderLink domainProviderLink = domainProviderLinkRepository.findByDomainNameAndProviderUrlAndProviderProviderTypeIdAndDeletedFalseAndEnabledTrue(domain.getName(), provider.getUrl(), provider.getProviderType().getId());
		if (domainProviderLink == null) {
			domainProviderLink = domainProviderLinkRepository.save(
				DomainProviderLink.builder()
				.domain(domain)
				.provider(provider)
				.enabled(true)
				.deleted(false)
				.ownerLink(ownerLink)
				.build()
			);
		}
		return domainProviderLink;
	}
}
