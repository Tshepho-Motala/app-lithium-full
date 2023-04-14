package service.casino.provider.cataboom.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.data.BonusAllocate;
import lombok.extern.slf4j.Slf4j;

@Service
@RestController

@EnableBinding(ProducerChannels.class)
@Slf4j
public class CATConversionQueueProcessor {

	private final MessageChannel consumer;

	public CATConversionQueueProcessor(ProducerChannels channels) {
		this.consumer = channels.consumer();
	}

	@RequestMapping("/process")
	public void handle(BonusAllocate ba) throws Exception {
		log.info(" " + ba.getBonusCode() + "," + ba.getPlayerGuid());

		try {
			this.consumer.send(MessageBuilder.<BonusAllocate>withPayload(ba).build());

			log.info("message sent");
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}

interface ProducerChannels {
	@Output
	MessageChannel consumer();
}
