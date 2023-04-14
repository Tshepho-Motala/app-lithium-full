package lithium.service.games.services;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.services.messagehandlers.objects.GameUserStatusRequest;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GameUserLockStatusService {
	@Autowired
	private GameUserStatusService gameUserStatusService;
	@Autowired
	private GatewayExchangeStream gatewayExchangeStream;
	
	@Async
	public void stream(String playerGuid) {
		log.debug("sending update request for user: "+playerGuid);
		final ObjectMapper mapper = new ObjectMapper();
		List<GameUserStatus> response;
		try {
			response = gameUserStatus(GameUserStatusRequest.builder().playerGuid(playerGuid).build());
			String json = mapper.writeValueAsString(response);
			log.debug("json : "+json);
			gatewayExchangeStream.process("playerroom/"+playerGuid, "gameuserstatus", json);
		} catch (Exception e) {
			log.error("Error streaming new content to playerroom/"+playerGuid, e);
		}
	}
	
	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(
					
				value = "gameuserstatus",
				durable = "false"
			),
			exchange = @Exchange(
				value = "gameuserstatus"
			),
			key = "gameuserstatus"
		),
		group="gameuserstatus"
	)
	public List<GameUserStatus> gameUserStatus(GameUserStatusRequest request) throws Exception {
		log.debug("GameUserStatus update request: "+request.toString());
		List<GameUserStatus> list = gameUserStatusService.findByUser(request.getPlayerGuid());
		
		list.stream().forEach(gus -> {
			gus.setEnabled(null);
			gus.getGame().setLockedMessage(null);
		});
		log.debug("sending GameUserStatus : "+list);
//		return GameUserStatusResponse.builder().gameUserStatusList(list).build();
		return list;
	}
}
