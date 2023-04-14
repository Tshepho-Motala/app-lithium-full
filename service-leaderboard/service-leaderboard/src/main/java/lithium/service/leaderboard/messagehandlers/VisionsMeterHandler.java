package lithium.service.leaderboard.messagehandlers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.services.LeaderboardService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VisionsMeterHandler {
	@Autowired LeaderboardService leaderboardService;
	
	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(value = "service-leaderboard-visionsmeter", durable = "false"),
			exchange = @Exchange(value = "service-leaderboard-visionsmeter"),
			key = "service-leaderboard-visionsmeter"
		)
	)
	public Response<Void> visions(ILeaderboardRequest request) {
		log.debug("Request: " + request.toString());
		Response<Void> response = new Response<>();
		try {
			Leaderboard leaderboard = leaderboardService.playerCurrentActiveLeaderboard(request.getPlayerGuid());
			if (leaderboard!=null) leaderboardService.streamVision(request.domainName(), leaderboard, request.getPlayerGuid());
			if (response.isSuccessful()) log.debug("Response: " + response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.setStatus(INTERNAL_SERVER_ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
}
