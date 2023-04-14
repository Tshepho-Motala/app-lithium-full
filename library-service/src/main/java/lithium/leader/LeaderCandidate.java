package lithium.leader;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.leader.client.LeaderClient;
import lithium.service.leader.objects.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LeaderCandidate {
	@Autowired private Environment environment;
	@Autowired private EurekaInstanceConfigBean eurekaInstanceConfigBean;
	@Autowired private LithiumConfigurationProperties properties;
	@Autowired private LithiumServiceClientFactory services;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
			new ThreadFactoryBuilder()
					.setNameFormat("leader-%d")
					.build());
	private boolean canBeLeaderCandidate;
	private String module;
	private String instanceId;
	private boolean shutdownInProgress = false;

	private Instance instance;

	public boolean iAmTheLeader() {
		return (instance == null)
				? false
				: instance.getLeader();
	}

	private void heartbeat() {
		log.trace("Transmitting heartbeat");
		boolean successful = true;
		try {
			Instance tempInstance = getClient().heartbeat(module, instanceId);
			if ((instance == null && tempInstance.getLeader()) ||
					(instance != null && !instance.getLeader() && tempInstance.getLeader())) {
				log.info("I am now the leader");
			} else if (instance != null && instance.getLeader() && tempInstance.getLeader()) {
				log.trace("I was already the leader");
			} else if (instance != null && instance.getLeader() && !tempInstance.getLeader()) {
				log.warn("I just lost leadership");
			} else if (instance != null && !instance.getLeader() && !tempInstance.getLeader()) {
				log.trace("I am still not the leader");
			}
			instance = tempInstance;
			successful = true;
		} catch (LithiumServiceClientFactoryException | HystrixRuntimeException e) {
			log.error("Unable to transmit heartbeat | {}", e.getMessage());
			successful = false;
		} catch (Exception e) {
			log.error("Unable to transmit heartbeat | {}", e.getMessage(), e);
			successful = false;
		} finally {
			if (!successful) {
				if (instance != null && instance.getLeader()) {
					log.warn("I just lost leadership because I was unable to coordinate with service-leader");
				}
				instance = null;
			}
		}
	}

	private void shutdown() {
		log.trace("Transmitting shutdown signal");
		try {
			getClient().shutdown(module, instanceId);
		} catch (LithiumServiceClientFactoryException | HystrixRuntimeException e) {
			log.error("Unable to transmit shutdown signal | {}", e.getMessage());
		} catch (Exception e) {
			log.error("Unable to transmit shutdown signal | {}", e.getMessage(), e);
		}
	}

	private void status() {
		if (iAmTheLeader()) {
			log.info("I am the leader");
		}
	}

	private LeaderClient getClient() throws LithiumServiceClientFactoryException {
		return services.target(LeaderClient.class, "service-leader", true);
	}

	@EventListener
	public void initialize(ApplicationStartedEvent event) {
		canBeLeaderCandidate = environment.getProperty("lithium.can-be-leader-candidate", Boolean.class,
				true);

		if (canBeLeaderCandidate) {
			module = environment.getProperty("spring.application.name");
			instanceId = eurekaInstanceConfigBean.getInstanceId();

			executor.scheduleAtFixedRate(() -> heartbeat(),
					10000,
					properties.getLeader().getHeartbeatMs(),
					TimeUnit.MILLISECONDS);

			executor.scheduleAtFixedRate(() -> status(),
					1000,
					60000,
					TimeUnit.MILLISECONDS);

			log.info("Leader candidate initialized | instanceId: {}, {}", instanceId, properties.getLeader());
		} else {
			log.warn("Leader candidate not initialized. The property lithium.can-be-leader-candidate is false");
		}
	}

	@EventListener
	public void shutdown(ContextClosedEvent event) {
		if (canBeLeaderCandidate && !shutdownInProgress) {
			log.info("Shutting down leader candidate");
			shutdownInProgress = true;
			shutdown();
			executor.shutdown();
			try {
				if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}
	}
}
