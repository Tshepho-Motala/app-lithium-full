package lithium.service.raf.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(RAFConversionStreamOutputQueue.class)
@ComponentScan
public class RAFConversionStreamConfiguration {
}