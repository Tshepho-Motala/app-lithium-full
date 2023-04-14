package lithium.shards;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lithium.config.LithiumConfigurationProperties;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.shards.client.ShardClient;
import lithium.service.shards.objects.Shard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ShardsRegistry {
	@Autowired private Environment environment;
	@Autowired private LithiumConfigurationProperties properties;
	@Autowired private LithiumServiceClientFactory services;

	private static final String CLASS = "ShardsRegistry";

	private boolean shutdownInProgress = false;
	private ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(
			new ThreadFactoryBuilder()
					.setNameFormat("shards-registry-%d")
					.build());
	// Last heartbeat timestamp is not a true reflection, it is not synchronized on every heartbeat
	private Map<String, Map<String, Shard>> pools = new ConcurrentHashMap<>();

	/**
	 *
	 * @param poolName The shard pool name, f.eg AdjustmentQueueProcessor
	 * @param key A unique key per shard within the pool, f.eg adjustmentqueue.adjustmentgroup-1,
	 *            adjustmentqueue.adjustmentgroup-2, ...
	 * @return A {@link Shard} containing a UUID. The shard will be kept alive until the {@link ShardsRegistry}
	 *         is destroyed, or it is manually removed from the pool by calling
	 *         {@link ShardsRegistry#remove(java.lang.String, java.lang.String)}
	 */
	@TimeThisMethod
	public Shard get(String poolName, String key) {
		SW.start(CLASS + ".getShardFromPool");
		Shard shard = getShardFromPool(poolName, key);
		SW.stop();
		if (shard == null) {
			try {
				SW.start(CLASS + ".get");
				shard = getClient().get(environment.getProperty("spring.application.name"), poolName);
				SW.stop();
				SW.start(CLASS + ".addShardToPool");
				addShardToPool(shard, poolName, key);
				SW.stop();
			} catch (LithiumServiceClientFactoryException | HystrixRuntimeException e) {
				String msg = "Unable to get a shard";
				// Failure:  SVC-shards is unhealthy and unable to service requests.
				// Solution: We create a shard object right here, it is not persisted to the service-shards repository,
				//           but it is better than throwing an exception causing consumers to fail.
				//           We add this shard to the respective pool as if nothing went wrong. Heartbeats will be
				//           attempted (when SVC-shards becomes healthy again) for these UUIDs but they do not exist
				//           on SVC-shards, which is fine. SVC-shards should ignore it.
				shard = Shard.builder().uuid(UUID.randomUUID().toString()).build();
				log.error(msg + " | pool: {} | fallback shard created: {} | {}", poolName, shard, e.getMessage(), e);
				addShardToPool(shard, poolName, key);
			}
		}
		return shard;
	}

	public void remove(String poolName, String key) {
		pools.computeIfPresent(poolName, (k, pool) -> {
			if (pool.remove(key) != null) {
				log.trace("Removed {} from pool {}", key, poolName);
			}
			return pool;
		});
	}

	private void addShardToPool(Shard shard, String poolName, String key) {
		pools.computeIfAbsent(poolName, k -> {
			log.trace("Added pool {}", poolName);
			return new ConcurrentHashMap<>();
		}).put(key, shard);
		log.trace("Shard added to pool | {}, poolName: {}, key: {}", shard, poolName, key);
	}

	private Shard getShardFromPool(String poolName, String key) {
		Map<String, Shard> pool = pools.get(poolName);
		if (pool == null) {
			log.trace("No pool {}", poolName);
			return null;
		}
		Shard shard = pool.get(key);
		if (shard == null) {
			log.trace("Key {} not found in pool {}", key, poolName);
			return null;
		}
		log.trace("Retrieved shard from pool | {} | {}, {}", shard, poolName, key);
		return shard;
	}

	private void heartbeat() {
		if (pools.isEmpty()) {
			log.trace("No shards yet");
		} else {
			log.trace("Transmitting heartbeat to keep the allocated shards alive | {}", pools);
			try {
				String module = environment.getProperty("spring.application.name");
				Map<String, Map<String, Shard>> updatedPools = getClient().bulkHeartbeat(module, pools);
				// Synchronize last heartbeat timestamp for pools in memory? ... Not necessary.
			} catch (Exception e) {
				log.error("Unable to transmit heartbeat | {}", e.getMessage(), e);
			}
		}
	}

	private ShardClient getClient() throws LithiumServiceClientFactoryException {
		return services.target(ShardClient.class, "service-shards", true);
	}

	@EventListener
	public void initialize(ApplicationStartedEvent event) {
		heartbeatExecutor.scheduleAtFixedRate(() -> heartbeat(),
				10000,
				properties.getShards().getHeartbeatMs(),
				TimeUnit.MILLISECONDS);
		log.info("Shards registry initialized | {}", properties.getShards());
	}

	@EventListener
	public void shutdown(ContextClosedEvent event) {
		if (!shutdownInProgress) shutdown();
	}

	private void shutdown() {
		log.info("Shutting down shards registry");
		shutdownInProgress = true;
		if (!pools.isEmpty()) {
			try {
				getClient().shutdown(environment.getProperty("spring.application.name"), pools);
				log.trace("Shutdown signal transmitted to service-shards");
			} catch (LithiumServiceClientFactoryException | HystrixRuntimeException e) {
				log.error("Failed to transmit shutdown signal to service-shards | {}", e.getMessage(), e);
			}
		}
		heartbeatExecutor.shutdown();
		try {
			if (!heartbeatExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				heartbeatExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			heartbeatExecutor.shutdownNow();
		}
	}
}
