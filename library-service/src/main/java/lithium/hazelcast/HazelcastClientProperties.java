package lithium.hazelcast;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.hazelcast-client")
@Data
public class HazelcastClientProperties {
	private Resource config;
	private Integer cacheOperationTimeoutMs;

	/**
	 * hazelcast.client.event.thread.count
	 *   
	 * Thread count for handling the incoming event packets.
	 */
	private Integer eventThreadCount;
	
	/**
	 * hazelcast.client.io.input.thread.count
	 *   
	 * Controls the number of I/O input threads. Defaults to -1, i.e., the system decides. 
	 * If the client is a smart client, it defaults to 3, otherwise it defaults to 1.
	 */
	private Integer ioInputThreadCount; 
	
	/**
	 * hazelcast.client.io.output.thread.count
	 *   
	 * Controls the number of I/O output threads. Defaults to -1, i.e., the system decides.
	 * If the client is a smart client, it defaults to 3, otherwise it defaults to 1.
	 */
	private Integer ioOutputThreadCount;
	
	/** 
	 * hazelcast.client.io.write.through
	 * Optimization that allows sending of packets over the network to be done on the calling 
	 * thread if the conditions are right. This can reduce the latency and increase the 
	 * performance for low threaded environments.
	 */
	private Boolean ioWriteThrough;
	
	/**
	 * hazelcast.client.response.thread.count
	 * 
	 * Number of the response threads. By default, there are two response threads; 
	 * this gives stable and good performance. If set to 0, the response threads are 
	 * bypassed and the response handling is done on the I/O threads. Under certain 
	 * conditions this can give a higher throughput, but setting to 0 should be regarded 
	 * as an experimental feature. If set to 0, the IO_OUTPUT_THREAD_COUNT is really 
	 * going to matter because the inbound thread will have more work to do. 
	 * By default when TLS is not enabled, there is just one inbound thread.
	 */
	private Integer responseThreadCount;
	
	/**
	 * hazelcast.client.response.thread.dynamic
	 * 
	 * Enables dynamic switching between processing the responses on the I/O threads and 
	 * offloading the response threads. Under certain conditions (single threaded clients) 
	 * processing on the I/O thread can increase the performance because useless handover 
	 * to the response thread is removed. Also the response thread is not created until it is needed. 
	 * Especially for ephemeral clients, reducing the threads can lead to increased 
	 * performance and reduced memory usage.
	 */
	private Boolean responseThreadDynamic;
	
	
	/**
	 * hazelcast.client.metrics.enabled
	 * 
	 * Enables the metrics collection if set to true, disables it otherwise. 
	 * Note that the preferred way for controlling this setting is Metrics Configuration.
	 */
	private Boolean metricsEnabled;
	
	/**
	 * hazelcast.metrics.collection.frequency
	 * 
	 * Frequency, in seconds, of the metrics collection cycle.
	 */
	private Integer metricsCollectionFrequency;
	
	/**
	 * hazelcast.client.metrics.jmx.enabled
	 * 
	 * Enables exposing the collected metrics over JMX if set to true, disables it otherwise.
	 */
	private Boolean metricsJmxEnabled;
	
}
