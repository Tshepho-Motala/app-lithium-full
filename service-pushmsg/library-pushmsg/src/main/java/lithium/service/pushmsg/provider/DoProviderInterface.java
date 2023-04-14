package lithium.service.pushmsg.provider;

import org.springframework.web.client.RestTemplate;

import lithium.service.pushmsg.client.internal.DeviceEditRequest;
import lithium.service.pushmsg.client.internal.DeviceEditResponse;
import lithium.service.pushmsg.client.internal.DeviceRequest;
import lithium.service.pushmsg.client.internal.DeviceResponse;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;

public interface DoProviderInterface {
	public DoProviderResponse send(DoProviderRequest request, RestTemplate restTemplate) throws Exception;
	public DeviceResponse deviceInfo(DeviceRequest request, RestTemplate restTemplate) throws Exception;
	public DeviceEditResponse editDevice(DeviceEditRequest request, RestTemplate restTemplate) throws Exception;
}