package lithium.service.leaderboard.messagehandlers;

import lithium.service.Response;
import lithium.service.leaderboard.data.dto.LeaderboardEntries;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.services.LeaderboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class LeaderboardHandler {
	@Autowired LeaderboardService leaderboardService;

	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(value = "service-leaderboard-player-active-leaderboard", durable = "false"),
			exchange = @Exchange(value = "service-leaderboard-player-active-leaderboard"),
			key = "service-leaderboard-player-active-leaderboard"
		)
	)
	public Response<LeaderboardEntries> playerActiveLeaderboard(ILeaderboardRequest request) {
		log.debug("Request: " + request.toString());
		Response<LeaderboardEntries> response = new Response<>();
		try {
			Leaderboard leaderboard = leaderboardService.playerCurrentActiveLeaderboard(request.getPlayerGuid());
			if (leaderboard == null) {
				response.setStatus(Response.Status.OK);
				response.setMessage("The player does not have a current active leaderboard");
			} else {
				LeaderboardEntries leaderboardEntries = leaderboardService.findEntriesFromLeaderboard(leaderboard);
				leaderboardService.enrichUserData(leaderboardEntries);
				leaderboardService.enrichBonusData(leaderboardEntries.getDomain().getName(), leaderboardEntries);
				response.setData(leaderboardEntries);
				response.setStatus(Response.Status.OK);
			}
			log.debug("Response: " + response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.setStatus(INTERNAL_SERVER_ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
}
