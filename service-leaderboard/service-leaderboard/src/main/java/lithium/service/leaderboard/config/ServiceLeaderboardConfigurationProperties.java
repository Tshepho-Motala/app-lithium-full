package lithium.service.leaderboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "lithium.services.leaderboard")
public class ServiceLeaderboardConfigurationProperties {
}