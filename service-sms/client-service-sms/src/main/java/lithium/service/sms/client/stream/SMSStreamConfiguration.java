package lithium.service.sms.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(SMSStreamOutputQueue.class)
@ComponentScan
public class SMSStreamConfiguration {
}