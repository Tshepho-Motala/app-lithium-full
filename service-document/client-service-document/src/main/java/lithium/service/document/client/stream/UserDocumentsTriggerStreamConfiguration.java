package lithium.service.document.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.user"})
@EnableBinding({UserDocumentsTriggerOutputQueue.class})
public class UserDocumentsTriggerStreamConfiguration {
}