package lithium.service.migration.config;

import lithium.service.migration.stream.limit.PlayerLimitPreferencesMigrationOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(PlayerLimitPreferencesMigrationOutputQueue.class)
@ComponentScan
public class PlayerLimitPreferenceStreamConfig {

}
