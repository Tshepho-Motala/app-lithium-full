package lithium.service.sms.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.sms.client.objects.DefaultSMSTemplate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultSMSTemplateStream {
	@Autowired DefaultSMSTemplateStreamOutputQueue channel;
	
	public void process(DefaultSMSTemplate defaultSMSTemplate) {
		try {
			channel.channel().send(MessageBuilder.<DefaultSMSTemplate>withPayload(defaultSMSTemplate).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}