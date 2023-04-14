package lithium.service.games.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(GamesStreamOutputQueue.class)
@ComponentScan
public class GamesStreamConfiguration {
}