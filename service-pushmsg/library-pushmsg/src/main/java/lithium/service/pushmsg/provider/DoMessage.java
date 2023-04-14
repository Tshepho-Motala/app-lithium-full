package lithium.service.pushmsg.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lithium.service.pushmsg.client.internal.DeviceEditRequest;
import lithium.service.pushmsg.client.internal.DeviceEditResponse;
import lithium.service.pushmsg.client.internal.DeviceRequest;
import lithium.service.pushmsg.client.internal.DeviceResponse;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DoMessage {
	@Autowired DoProviderInterface provider;
	@Autowired
	@Qualifier("lithium.service.pushmsg.RestTemplate")
	private RestTemplate restTemplate;
	
	public DoProviderResponse send(DoProviderRequest request) throws Exception {
		return provider.send(request, restTemplate);
	}
	
	public DeviceResponse deviceInfo(DeviceRequest request) throws Exception {
		log.info("DeviceRequest : "+request);
		return provider.deviceInfo(request, restTemplate);
	}
	
	public DeviceEditResponse editDevice(DeviceEditRequest request) throws Exception {
		log.info("DeviceEditRequest : "+request);
		return provider.editDevice(request, restTemplate);
	}
}