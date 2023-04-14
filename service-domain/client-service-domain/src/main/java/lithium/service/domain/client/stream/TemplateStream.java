package lithium.service.domain.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.domain.client.objects.Template;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TemplateStream {
	@Autowired
	private TemplateOutputQueue channel;
	
	public void process(Template template) {
		try {
			channel.channel().send(MessageBuilder.<Template>withPayload(template).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}