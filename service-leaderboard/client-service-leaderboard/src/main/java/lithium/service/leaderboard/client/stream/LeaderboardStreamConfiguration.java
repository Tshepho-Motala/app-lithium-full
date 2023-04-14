package lithium.service.leaderboard.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.leaderboard"})
@EnableBinding({LeaderboardOutputQueue.class})
public class LeaderboardStreamConfiguration {
}