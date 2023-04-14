package lithium.service.casino.provider.slotapi.services;

import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataMigrationService {
	@Autowired private BetRepository betRepository;

	public List<Bet> fetchBets(Long start, Long end) {
		return betRepository.findByIdBetweenOrderByIdAsc(start, end);
	}
}
