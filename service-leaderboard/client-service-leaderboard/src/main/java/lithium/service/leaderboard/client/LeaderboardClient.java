package lithium.service.leaderboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient(name="service-leaderboard")
public interface LeaderboardClient {
	@RequestMapping(path="/leaderboard/admin/optout", method=RequestMethod.POST)
	public Response<Void> optout(@RequestParam(name="guid") String guid);
}