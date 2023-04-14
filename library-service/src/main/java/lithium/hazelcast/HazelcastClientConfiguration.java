package lithium.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.properties.ClientProperty;
import com.hazelcast.core.HazelcastInstance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@ComponentScan
@EnableConfigurationProperties({ HazelcastClientProperties.class })
@AllArgsConstructor
@Slf4j
public class HazelcastClientConfiguration implements NamedInstance {

    HazelcastClientProperties cacheProperties;

    Environment environment;

    @Override
    public Class<?> getClazz() {
        return this.getClass();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

	@Bean
	public HazelcastInstance hazelcastInstance() throws IOException, IllegalAccessException {
		// With jdk9+, reflective access into jdk interals became more restrictive. Hazelcast does some optimisation
		// that requires such access, explained here:https://github.com/hazelcast/hazelcast/blob/14ef0dbefa4a6fa00a50a8873aeea02ec6c3f5e4/hazelcast/src/main/java/com/hazelcast/internal/networking/nio/SelectorOptimizer.java#L35.

		// Fortunately, a system property was available to turn off this optimisation, found here: https://github.com/hazelcast/hazelcast/blob/14ef0dbefa4a6fa00a50a8873aeea02ec6c3f5e4/hazelcast/src/main/java/com/hazelcast/internal/networking/nio/SelectorOptimizer.java#L65.

		// As a workaround, when a HazelcastInstance bean is initiated, we add the system property to disable
		// this optimisation.
		System.setProperty("hazelcast.io.optimizeselector", "false");

		Resource configFile = cacheProperties.getConfig();
		log.info("Hazelcast client config location: {}", configFile);
		ClientConfig clientConfig = new XmlClientConfigBuilder(configFile.getInputStream()).build();
		clientConfig.setInstanceName(getName());
		
		if (cacheProperties.getMetricsEnabled() != null) {
			clientConfig.getMetricsConfig().setEnabled(cacheProperties.getMetricsEnabled());
		}
		
		if (cacheProperties.getMetricsCollectionFrequency() != null) {
			clientConfig.getMetricsConfig().setCollectionFrequencySeconds(cacheProperties.getMetricsCollectionFrequency());
		}
		
		if (cacheProperties.getMetricsJmxEnabled() != null) {
			clientConfig.getMetricsConfig().getJmxConfig().setEnabled(cacheProperties.getMetricsJmxEnabled());
		}
		
		if (cacheProperties.getIoWriteThrough() != null) {
			clientConfig.setProperty(ClientProperty.IO_WRITE_THROUGH_ENABLED.getName(), 
					cacheProperties.getIoWriteThrough() ? "true":"false");
		}
		
		if (cacheProperties.getResponseThreadDynamic() != null) {
			clientConfig.setProperty(ClientProperty.RESPONSE_THREAD_DYNAMIC.getName(), 
					cacheProperties.getResponseThreadDynamic() ? "true":"false");
		}

		if (cacheProperties.getEventThreadCount() != null) {
			clientConfig.setProperty(ClientProperty.EVENT_THREAD_COUNT.getName(), 
					cacheProperties.getEventThreadCount().toString());
		}

		if (cacheProperties.getIoInputThreadCount() != null) {
			clientConfig.setProperty(ClientProperty.IO_INPUT_THREAD_COUNT.getName(), 
					cacheProperties.getIoInputThreadCount().toString());
		}

		if (cacheProperties.getIoOutputThreadCount() != null) {
			clientConfig.setProperty(ClientProperty.IO_OUTPUT_THREAD_COUNT.getName(), 
					cacheProperties.getIoOutputThreadCount().toString());
		}

		if (cacheProperties.getResponseThreadCount() != null) {
			clientConfig.setProperty(ClientProperty.RESPONSE_THREAD_COUNT.getName(), 
					cacheProperties.getResponseThreadCount().toString());
		}
		
		log.info("Hazelcast Client Properties: " + clientConfig.toString());

		HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);

                // Using a `@Bean` decorated method not being created before JDBC/JPA starts getting instantiated
                HazelcastSingleton.getInstance().setHazelcast(instance);

        return instance;
    }

}
