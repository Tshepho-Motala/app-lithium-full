package lithium.service.access.controllers;

import java.util.ArrayList;
import java.util.List;

import lithium.service.access.client.objects.UserDuplicatesTypes;
import lithium.service.access.client.objects.SimpleUserDuplicateType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.client.objects.SimpleBrowser;
import lithium.service.access.client.objects.SimpleOperatingSystem;

@RestController
@RequestMapping("/frontend/helper")
public class FrontendHelperController {
	
	@GetMapping("/browsers")
	public Response<List<SimpleBrowser>> browsers() {
		List<SimpleBrowser> browsers = new ArrayList<SimpleBrowser>();
		for (Browser browser: Browser.values()) {
			browsers.add(SimpleBrowser.builder()
							.name(browser.getName())
							.value(browser.getName())
							.build());
		}
		browsers.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
		return Response.<List<SimpleBrowser>>builder()
				.data(browsers)
				.status(Status.OK)
				.build();
	}
	
	@GetMapping("/operatingSystems")
	public Response<List<SimpleOperatingSystem>> operatingSystems() {
		List<SimpleOperatingSystem> operatingSystems = new ArrayList<SimpleOperatingSystem>();
		for (OperatingSystem operatingSystem: OperatingSystem.values()) {
			operatingSystems.add(SimpleOperatingSystem.builder()
									.name(operatingSystem.getName())
									.value(operatingSystem.getName())
									.build());
		}
		operatingSystems.sort((os1, os2) -> os1.getName().compareTo(os2.getName()));
		return Response.<List<SimpleOperatingSystem>>builder()
				.data(operatingSystems)
				.status(Status.OK)
				.build();
	}

  @GetMapping("/duplicate-types")
  public Response<List<SimpleUserDuplicateType>> duplicateTypes() {
    List<SimpleUserDuplicateType> simpleUserDuplicateTypes = new ArrayList<SimpleUserDuplicateType>();
    for (UserDuplicatesTypes duplicateType: UserDuplicatesTypes.values()) {
      simpleUserDuplicateTypes.add(SimpleUserDuplicateType.builder()
          .name(duplicateType.getName())
          .value(duplicateType.getName())
          .build());
    }
    return Response.<List<SimpleUserDuplicateType>>builder()
        .data(simpleUserDuplicateTypes)
        .status(Status.OK)
        .build();
  }
}
