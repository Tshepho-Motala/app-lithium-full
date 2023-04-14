package lithium.service.casino.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.data.entities.BonusUnlockGames;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusUnlockGamesRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CasinoBonusUnlockGamesService {
	@Autowired 
	private BonusUnlockGamesRepository bonusUnlockGamesRepository;
	@Autowired
	private LithiumServiceClientFactory services;
	
	private GamesClient getGamesClientService() {
		GamesClient gc = null;
		try {
			gc = services.target(GamesClient.class, false);
		} catch (Exception e) {
			log.error("Problem getting GamesClient service", e);
		}
		return gc;
	}
	
	private void unlockGamesForPlayer(String gameId, String playerGuid) {
		User user = User.builder().guid(playerGuid).build();
		getGamesClientService().toggleLocked(Long.valueOf(gameId), user);
	}
	
	public List<BonusUnlockGames> unlockGames(Long bonusRevisionId) {
		return bonusUnlockGamesRepository.findByBonusRevisionId(bonusRevisionId);
	}
	
	public BonusUnlockGames saveUnlockGames(BonusUnlockGames game) {
		return bonusUnlockGamesRepository.save(game);
	}
	
	public void deleteUnlockGames(Long id) {
		bonusUnlockGamesRepository.deleteById(id);
	}
	
	public void triggerUnlockGames(PlayerBonusHistory pbh) {
		List<BonusUnlockGames> listToUnlock = bonusUnlockGamesRepository.findByBonusRevisionId(pbh.getBonus().getId());
		String playerGuid = pbh.getPlayerBonus().getPlayerGuid();
		for (BonusUnlockGames bug:listToUnlock) {
			unlockGamesForPlayer(bug.getGameId(), playerGuid);
		}
	}
}
