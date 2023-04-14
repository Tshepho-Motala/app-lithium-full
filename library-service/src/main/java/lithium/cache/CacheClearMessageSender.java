package lithium.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheClearMessageSender {
	@Autowired
	private CacheClearMessageSenderChannel channel;
	
	public void clearCache(String cacheNameRegex) {
		log.warn("clearCache : "+cacheNameRegex);
		channel.channel().send(MessageBuilder.<String>withPayload(cacheNameRegex).build());
	}
}