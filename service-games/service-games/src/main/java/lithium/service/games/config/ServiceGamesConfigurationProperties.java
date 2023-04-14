package lithium.service.games.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;
import java.util.HashMap;

@ConfigurationProperties(prefix = "lithium.services.games")
@Data
public class ServiceGamesConfigurationProperties {
//	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
//	@Valid
//	Long avatarMaxSize;
    ChannelMigrationJob channelMigrationJob = new ChannelMigrationJob();

    @Data
    public static class ChannelMigrationJob {
        private boolean enabled;
    }
}
