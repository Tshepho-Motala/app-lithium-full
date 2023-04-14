package lithium.service.sms.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.sms.data.entities.Provider;
import lithium.service.sms.data.entities.ProviderProperty;
import lithium.service.sms.services.ProviderService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(ProviderRegisterQueueSink.class)
@Slf4j
public class ProviderRegisterQueueProcessor {
	@Autowired ProviderService providerService;
	
	@StreamListener(ProviderRegisterQueueSink.INPUT)
	void handle(lithium.service.sms.client.objects.Provider sp) throws Exception {
		log.info("Provider received for registration: " + sp.getName());
		
		Provider p = providerService.findByCode(sp.getCode());
		if (p == null) p = new Provider();
		
		p.setCode(sp.getCode());
		p.setName(sp.getName());
		p.setUrl(sp.getUrl());
		p.setEnabled(sp.getEnabled());
		
		p = providerService.save(p);
		
		for (lithium.service.sms.client.objects.ProviderProperty spp: sp.getProperties()) processProperty(spp, p);
	}
	
	private ProviderProperty processProperty(lithium.service.sms.client.objects.ProviderProperty spp, Provider p) {
		ProviderProperty pp = providerService.findPropertyByProviderIdAndName(p, spp.getName());
		if (pp == null) pp = ProviderProperty.builder().provider(p).name(spp.getName()).build();
		pp.setDefaultValue(spp.getDefaultValue());
		pp.setDescription(spp.getDescription());
		pp.setType(spp.getType());
		pp = providerService.saveProperty(pp);
		return pp;
	}
}