package lithium.service.datafeed.provider.google.service;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBinding(PubSubExchangeQueue.class)
public class PubSubExchangeStreamConfiguration {
}