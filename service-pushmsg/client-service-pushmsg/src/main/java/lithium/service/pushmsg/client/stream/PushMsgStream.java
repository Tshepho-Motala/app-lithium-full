package lithium.service.pushmsg.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.pushmsg.client.objects.PushMsgBasic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PushMsgStream {
	@Autowired PushMsgStreamOutputQueue channel;
	
	public void process(PushMsgBasic pushMsgBasic) {
		try {
			channel.channel().send(MessageBuilder.<PushMsgBasic>withPayload(pushMsgBasic).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}