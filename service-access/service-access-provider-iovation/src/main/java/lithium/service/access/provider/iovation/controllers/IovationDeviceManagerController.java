package lithium.service.access.provider.iovation.controllers;

import lithium.service.access.provider.iovation.data.DeviceRegistrationResponse;
import lithium.service.access.provider.iovation.services.IovationDeviceManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.access.provider.iovation.data.DeviceRegistrationRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/external/iovation/devices/")
@Slf4j
public class IovationDeviceManagerController {
	@Autowired IovationDeviceManagerService iovationDeviceManagerService;

	public boolean registerDevice(String domainName, DeviceRegistrationRequest registrationRequest) throws Exception {
		DeviceRegistrationResponse deviceRegistrationResponse = iovationDeviceManagerService.registerDevice(domainName, registrationRequest);
		// TODO: 2019/09/18 We should maybe add some checks in here but for now this will do.
		return true;
	}
}
