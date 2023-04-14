package lithium.service.shards.controllers.system;

import lithium.service.shards.client.ShardClient;
import lithium.service.shards.exceptions.Status404ShardNotFoundException;
import lithium.service.shards.objects.Shard;
import lithium.service.shards.services.ShardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/shards")
public class ShardController implements ShardClient {
	@Autowired private ShardService service;

	@RequestMapping(path = "/{module}/{pool}/get", method = RequestMethod.GET)
	@Override
	public Shard get(@PathVariable("module") String module, @PathVariable("pool") String pool) {
		return service.get(module, pool);
	}

	@RequestMapping(path = "/{module}/shutdown", method = RequestMethod.POST)
	@Override
	public void shutdown(@PathVariable("module") String module, @RequestBody Map<String, Map<String, Shard>> pools) {
		service.shutdown(module, pools);
	}

	@RequestMapping(path = "/{module}/{pool}/heartbeat/{uuid}", method = RequestMethod.POST)
	@Override
	public Shard heartbeat(@PathVariable("module") String module, @PathVariable("pool") String pool,
	        @PathVariable("uuid") String uuid) throws Status404ShardNotFoundException {
		return service.heartbeat(module, pool, uuid);
	}

	@RequestMapping(path = "/{module}/bulk-heartbeat", method = RequestMethod.POST)
	@Override
	public Map<String, Map<String, Shard>> bulkHeartbeat(@PathVariable("module") String module,
			@RequestBody Map<String, Map<String, Shard>> pools) {
		return service.bulkHeartbeat(module, pools);
	}

	@RequestMapping(path = "/{module}/{pool}/bulk-heartbeat", method = RequestMethod.POST)
	@Override
	public List<Shard> bulkHeartbeat(@PathVariable("module") String module, @PathVariable("pool") String pool,
	        @RequestParam("uuids") List<String> uuids) {
		return service.bulkHeartbeat(module, pool, uuids);
	}
}
