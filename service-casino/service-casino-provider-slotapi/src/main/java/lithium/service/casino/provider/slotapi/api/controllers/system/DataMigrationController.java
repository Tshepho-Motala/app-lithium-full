package lithium.service.casino.provider.slotapi.api.controllers.system;

import lithium.service.casino.provider.slotapi.services.DataMigrationService;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/data-migration")
public class DataMigrationController {
	@Autowired private DataMigrationService service;

	@GetMapping("/fetch-bets")
	private List<Bet> fetchBets(@RequestParam("start") Long start, @RequestParam("end") Long end) {
		return service.fetchBets(start, end);
	}
}
