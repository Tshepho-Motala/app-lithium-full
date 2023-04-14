package lithium.service.pushmsg.services.provider;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.pushmsg.client.internal.DoProviderClient;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;
import lithium.service.pushmsg.data.entities.DomainProvider;
import lithium.service.pushmsg.data.entities.DomainProviderProperty;
import lithium.service.pushmsg.data.repositories.PushMsgRepository;
import lithium.service.pushmsg.services.DomainProviderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProvider {
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired DomainProviderService dpService;
	@Autowired PushMsgRepository pushMsgRepository;
	
	public DoProviderResponse run(DomainProvider domainProvider, DoProviderRequest request) throws Exception {
		try {
			DoProviderClient client = serviceFactory.target(DoProviderClient.class, domainProvider.getProvider().getUrl(), true);
			HashMap<String, String> properties = new HashMap<>();
			for (DomainProviderProperty prop: dpService.propertiesWithDefaults(domainProvider.getId())) {
				properties.put(prop.getProviderProperty().getName(), prop.getValue());
			}
			request.setProperties(properties);
			return client.send(request);
		} catch (Exception e) {
			log.error("Call to provider failed. " + e.getMessage(), e);
			return null;
		}
	}
	
	public void processProviderCallback(DoProviderResponse response) throws Exception {
		log.info("Received callback from provider: " + response.toString());
		
//		PushMsg pushMsg = null;
//		if (response.getProviderId() != null) {
//			pushMsg = pushMsgRepository.findOne(response.getProviderId());
//			if (pushMsg == null) {
//				throw new Exception("Could not find sms by id");
//			}
//		} else {
//			if (response.getProviderReference() == null || response.getProviderCode() == null) {
//				throw new Exception("No sms id or providerReference and providerCode pair");
//			}
//			pushMsg = pushMsgRepository.findByProviderReferenceAndDomainProviderProviderCode(response.getProviderReference(), response.getProviderCode());
//			if (pushMsg == null) {
//				throw new Exception("Could not find sms by provider reference and provider code");
//			}
//		}
//		if (response.getStatus().getCode().equals(DoProviderResponseStatus.SUCCESS.getCode())) {
//			pushMsg.setReceivedDate(new Date());
//		} else if (response.getStatus().getCode().equals(DoProviderResponseStatus.FAILED.getCode())) {
//			pushMsg.setLatestErrorReason(response.getMessage());
//		}
//		pushMsgRepository.save(pushMsg);
	}
}