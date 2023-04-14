package lithium.service.casino.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.data.BonusAllocate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TriggerBonusStream {
	@Autowired TriggerBonusStreamOutputQueue channel;
	
	public void process(BonusAllocate bonusAllocate) {
		try {
			channel.channel().send(MessageBuilder.<BonusAllocate>withPayload(bonusAllocate).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}