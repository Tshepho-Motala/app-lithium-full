package lithium.service.pushmsg.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.pushmsg.client.internal.DeviceEditRequest;
import lithium.service.pushmsg.client.internal.DeviceEditResponse;
import lithium.service.pushmsg.client.internal.DeviceRequest;
import lithium.service.pushmsg.client.internal.DeviceResponse;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/internal/do")
public class DoProviderController {
	@Autowired DoMessage doMessage;
	
	@RequestMapping("/send")
	public DoProviderResponse request(@RequestBody DoProviderRequest request) throws Exception {
		return doMessage.send(request);
	}
	
	@RequestMapping("/deviceinfo")
	public DeviceResponse deviceInfo(@RequestBody DeviceRequest request) throws Exception {
		log.info("DeviceRequest : "+request);
		return doMessage.deviceInfo(request);
	}
	
	@RequestMapping("/editdevice")
	public DeviceEditResponse editDevice(@RequestBody DeviceEditRequest request) throws Exception {
		log.info("DeviceEditRequest : "+request);
		return doMessage.editDevice(request);
	}
}