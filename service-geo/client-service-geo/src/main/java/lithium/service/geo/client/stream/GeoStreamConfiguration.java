package lithium.service.geo.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(GeoOutputQueue.class)
@ComponentScan
public class GeoStreamConfiguration {
}
