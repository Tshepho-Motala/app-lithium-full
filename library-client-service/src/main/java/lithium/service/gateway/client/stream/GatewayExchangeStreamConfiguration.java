package lithium.service.gateway.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(GatewayExchangeStreamOutputQueue.class)
@ComponentScan
public class GatewayExchangeStreamConfiguration {
}