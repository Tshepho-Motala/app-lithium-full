package lithium.service.migration.config;

import lithium.service.migration.stream.limit.RealityCheckMigrationOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(RealityCheckMigrationOutputQueue.class)
@ComponentScan

public class RealityCheckStreamConfig {

}
