package lithium.service.domain.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBinding(ProvidersStreamOutputQueue.class)
@ComponentScan
public class ProvidersStreamConfiguration {

	public ProvidersStreamConfiguration() {
		super();
		log.info("ProvidersStreamConfiguration");
	}

}