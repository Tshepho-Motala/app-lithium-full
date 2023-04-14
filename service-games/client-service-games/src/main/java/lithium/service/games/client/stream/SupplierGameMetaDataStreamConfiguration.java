package lithium.service.games.client.stream;

import lithium.service.domain.client.stream.DomainEventsOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(SupplierGameMetaDataOutputQueue.class)
@ComponentScan
public class SupplierGameMetaDataStreamConfiguration {
}