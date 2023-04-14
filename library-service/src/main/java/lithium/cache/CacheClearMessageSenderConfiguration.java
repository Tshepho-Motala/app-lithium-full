package lithium.cache;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(CacheClearMessageSenderChannel.class)
public class CacheClearMessageSenderConfiguration {

	@Bean CacheClearMessageSender sender() {
		return new CacheClearMessageSender();
	}
}
