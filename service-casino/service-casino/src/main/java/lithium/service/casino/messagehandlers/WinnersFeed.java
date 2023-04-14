package lithium.service.casino.messagehandlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;

import lithium.service.casino.data.entities.Winner;
import lithium.service.casino.data.objects.FrontendWinner;
import lithium.service.casino.messagehandlers.objects.ScratchWinnersFeedRequest;
import lithium.service.casino.service.WinnerFeedService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WinnersFeed {
	
	@Autowired
	WinnerFeedService winnerFeedService;

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "winnersfeedlist", durable = "false"),
			exchange = @Exchange(value = "winnersfeedlist"),
			key = "winnersfeedlist"), group="winnersfeedlist")
	public ArrayList<FrontendWinner> winnersFeedList(WinnersFeedRequest request) throws InterruptedException {
		return (ArrayList<FrontendWinner>) winnerFeedService.getFrontendWinnersList(request.getDomainName());
	}
	
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "scratchcardwinners", durable = "false"),
			exchange = @Exchange(value = "scratchcardwinners"),
			key = "scratchcardwinners"), group="scratchcardwinners")
	public ArrayList<FrontendWinner> scratchWinnersFeedList(ScratchWinnersFeedRequest request) throws InterruptedException, JsonProcessingException {
		log.debug("Scratch join request: " + request.toString());
		//return (new ObjectMapper()).writeValueAsString(winnerFeedService.getWinnersList(request.getDomain()));
		return (ArrayList<FrontendWinner>) winnerFeedService.getFrontendWinnersList(request.getDomain());
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class WinnersFeedRequest {
		private String domainName;
	}
}
