package lithium.service.shards.client;

import lithium.service.shards.exceptions.Status404ShardNotFoundException;
import lithium.service.shards.objects.Shard;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Do not use this interface directly. Rather, @Autowire a {@link lithium.shards.ShardsRegistry} and
 * use {@link lithium.shards.ShardsRegistry#get(java.lang.String, java.lang.String)}. It will automatically
 * handle heartbeat transmission. Nothing stops you from using this interface, but if you were to do so, you
 * will have to manage the shards and heartbeat transmission. If the heartbeat is not transmitted within
 * certain thresholds, then you risk the shard being shared among other processes or eventually killed.
 */

@FeignClient(name = "service-shards")
public interface ShardClient {
	@RequestMapping(path = "/system/shards/{module}/{pool}/get", method = RequestMethod.GET)
	public Shard get(@PathVariable("module") String module, @PathVariable("pool") String pool);

	@RequestMapping(path = "/system/shards/{module}/shutdown", method = RequestMethod.POST)
	public void shutdown(@PathVariable("module") String module, @RequestBody Map<String, Map<String, Shard>> pools);

	@RequestMapping(path = "/system/shards/{module}/{pool}/heartbeat/{uuid}", method = RequestMethod.POST)
	public Shard heartbeat(@PathVariable("module") String module, @PathVariable("pool") String pool,
			@PathVariable("uuid") String uuid) throws Status404ShardNotFoundException;

	@RequestMapping(path = "/system/shards/{module}/bulk-heartbeat", method = RequestMethod.POST)
	public Map<String, Map<String, Shard>> bulkHeartbeat(@PathVariable("module") String module,
			@RequestBody Map<String, Map<String, Shard>> pools);

	@RequestMapping(path = "/system/shards/{module}/{pool}/bulk-heartbeat", method = RequestMethod.POST)
	public List<Shard> bulkHeartbeat(@PathVariable("module") String module, @PathVariable("pool") String pool,
			@RequestParam("uuids") List<String> uuids);
}
