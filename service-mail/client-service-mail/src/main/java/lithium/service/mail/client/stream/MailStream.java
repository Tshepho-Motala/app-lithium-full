package lithium.service.mail.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.mail.client.objects.EmailData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailStream {
	@Autowired MailStreamOutputQueue channel;
	
	public void process(EmailData emailData) {
		try {
			channel.channel().send(MessageBuilder.<EmailData>withPayload(emailData).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}