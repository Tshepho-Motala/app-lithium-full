package lithium.service.sms.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.sms.client.objects.SMSBasic;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SMSStream {
	@Autowired SMSStreamOutputQueue channel;
	
	public void process(SMSBasic smsBasic) {
		try {
			channel.channel().send(MessageBuilder.<SMSBasic>withPayload(smsBasic).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}