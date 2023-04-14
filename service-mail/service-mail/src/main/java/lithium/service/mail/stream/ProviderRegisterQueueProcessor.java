package lithium.service.mail.stream;

import lithium.service.mail.data.entities.ProviderType;
import lithium.service.mail.data.repositories.ProviderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.mail.data.entities.Provider;
import lithium.service.mail.data.entities.ProviderProperty;
import lithium.service.mail.services.ProviderService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(ProviderRegisterQueueSink.class)
@Slf4j
public class ProviderRegisterQueueProcessor {
	@Autowired ProviderService providerService;

	@Autowired
	ProviderTypeRepository providerTypeRepository;
	
	@StreamListener(ProviderRegisterQueueSink.INPUT)
	void handle(lithium.service.mail.client.objects.Provider sp) throws Exception {
		log.debug("Provider received for registration: " + sp.getName());

		ProviderType providerType = providerTypeRepository.findOneByName(sp.getProviderType().getName());
		
		Provider p = providerService.findByCode(sp.getCode());
		if (p == null) p = new Provider();
		
		p.setCode(sp.getCode());
		p.setName(sp.getName());
		p.setUrl(sp.getUrl());
		p.setEnabled(sp.getEnabled());
		p.setProviderType(providerType);
		
		p = providerService.save(p);
		
		for (lithium.service.mail.client.objects.ProviderProperty spp: sp.getProperties()) processProperty(spp, p);
	}
	
	private ProviderProperty processProperty(lithium.service.mail.client.objects.ProviderProperty spp, Provider p) {
		ProviderProperty pp = providerService.findPropertyByProviderIdAndName(p, spp.getName());
		if (pp == null) pp = ProviderProperty.builder().provider(p).name(spp.getName()).build();
		pp.setDefaultValue(spp.getDefaultValue());
		pp.setDescription(spp.getDescription());
		pp.setType(spp.getType());
		pp = providerService.saveProperty(pp);
		return pp;
	}
}