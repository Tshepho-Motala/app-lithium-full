package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.GameGraphic;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameGraphicRepository extends PagingAndSortingRepository<GameGraphic, Long> {
	GameGraphic findByGameIdAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(long gameId, long graphicFunctionId);
	GameGraphic findByGameIdAndEnabledTrueAndDeletedFalseAndLiveCasinoAndGraphicFunctionId(long gameId, boolean liveCasino, long graphicFunctionId);
}
