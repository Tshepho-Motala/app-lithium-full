package lithium.service.games.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.games.client.objects.GameStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GamesStream {
	@Autowired
	private GamesStreamOutputQueue channel;
	
	public void registerGame(GameStream game) {
		log.info("Sending Game : "+game);
		channel.channel().send(MessageBuilder.<GameStream>withPayload(game).build());
	}
}
