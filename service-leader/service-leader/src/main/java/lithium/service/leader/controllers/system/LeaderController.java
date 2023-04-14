package lithium.service.leader.controllers.system;

import lithium.service.leader.client.LeaderClient;
import lithium.service.leader.objects.Instance;
import lithium.service.leader.services.LeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/leader")
public class LeaderController implements LeaderClient {
	@Autowired private LeaderService service;

	@PostMapping("/heartbeat")
	@Override
	public Instance heartbeat(@RequestParam("module") String module, @RequestParam("instanceId") String instanceId) {
		return service.heartbeat(module, instanceId);
	}

	@PostMapping("/shutdown")
	@Override
	public void shutdown(@RequestParam("module") String module, @RequestParam("instanceId") String instanceId) {
		service.shutdown(module, instanceId);
	}
}
