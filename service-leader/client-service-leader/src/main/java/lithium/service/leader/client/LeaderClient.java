package lithium.service.leader.client;

import lithium.service.leader.objects.Instance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-leader")
public interface LeaderClient {
	@RequestMapping(path = "/system/leader/heartbeat", method = RequestMethod.POST)
	public Instance heartbeat(@RequestParam("module") String module, @RequestParam("instanceId") String instanceId);

	@RequestMapping(path = "/system/leader/shutdown", method = RequestMethod.POST)
	public void shutdown(@RequestParam("module") String module, @RequestParam("instanceId") String instanceId);
}
