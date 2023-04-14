package lithium.service.sms.services.provider;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.sms.client.internal.DoProviderClient;
import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.data.entities.DomainProvider;
import lithium.service.sms.data.entities.DomainProviderProperty;
import lithium.service.sms.data.entities.SMS;
import lithium.service.sms.data.repositories.SMSRepository;
import lithium.service.sms.services.DomainProviderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProvider {
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired DomainProviderService dpService;
	@Autowired SMSRepository smsRepository;
	
	public DoProviderResponse run(DomainProvider domainProvider, DoProviderRequest request) throws Exception {
		try {
			DoProviderClient client = serviceFactory.target(DoProviderClient.class, domainProvider.getProvider().getUrl(), true);
			HashMap<String, String> properties = new HashMap<>();
			for (DomainProviderProperty prop: dpService.propertiesWithDefaults(domainProvider.getId())) {
				properties.put(prop.getProviderProperty().getName(), prop.getValue());
			}
			request.setProperties(properties);
			return client.doPost(request);
		} catch (Exception e) {
			log.error("Call to provider failed. " + e.getMessage(), e);
			return null;
		}
	}
	
	public void processProviderCallback(DoProviderResponse response) throws Exception {
		log.info("Received callback from provider: " + response.toString());
		
		SMS sms = null;
		if (response.getSmsId() != null) {
			sms = smsRepository.findOne(response.getSmsId());
			if (sms == null) {
				throw new Exception("Could not find sms by id");
			}
		} else {
			if (response.getProviderReference() == null || response.getProviderCode() == null) {
				throw new Exception("No sms id or providerReference and providerCode pair");
			}
			sms = smsRepository.
					findByProviderReferenceAndDomainProviderProviderCode(response.getProviderReference(), response.getProviderCode());
			if (sms == null) {
				throw new Exception("Could not find sms by provider reference and provider code");
			}
		}
		if (response.getStatus().getCode().equals(DoProviderResponseStatus.SUCCESS.getCode())) {
			sms.setReceivedDate(new Date());
		} else if (response.getStatus().getCode().equals(DoProviderResponseStatus.FAILED.getCode())) {
			sms.setErrorCount(sms.getErrorCount() + 1);
			sms.setLatestErrorReason(response.getMessage());
		}
		smsRepository.save(sms);
	}
}