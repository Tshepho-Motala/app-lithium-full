package lithium.service.pushmsg.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-pushmsg-provider")
public interface DoProviderClient {
	@RequestMapping(path="/internal/do/send", method=RequestMethod.POST) 
	public DoProviderResponse send(@RequestBody DoProviderRequest request);
	
	@RequestMapping(path="/internal/do/deviceinfo", method=RequestMethod.POST) 
	public DeviceResponse deviceInfo(@RequestBody DeviceRequest request);
	
	@RequestMapping(path="/internal/do/editdevice", method=RequestMethod.POST) 
	public DeviceEditResponse editDevice(@RequestBody DeviceEditRequest request);
}