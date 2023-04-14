package lithium.service.mail.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.mail.client.objects.DefaultEmailTemplate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultEmailTemplateStream {
	@Autowired DefaultEmailTemplateStreamOutputQueue channel;
	
	public void process(DefaultEmailTemplate defaultEmailTemplate) {
		try {
			channel.channel().send(MessageBuilder.<DefaultEmailTemplate>withPayload(defaultEmailTemplate).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}