package lithium.service.casino.client;

import lithium.service.casino.client.objects.slotapi.Bet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-casino-provider-slotapi")
public interface SlotApiDataMigrationClient {
	@RequestMapping(method = RequestMethod.GET, value = "/system/data-migration/fetch-bets")
	public List<Bet> fetchBets(@RequestParam("start") Long start, @RequestParam("end") Long end);
}
