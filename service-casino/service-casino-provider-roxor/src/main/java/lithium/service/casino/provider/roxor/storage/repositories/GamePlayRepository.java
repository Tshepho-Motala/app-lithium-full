package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface GamePlayRepository extends FindOrCreateByGuidRepository<GamePlay, Long> {
    GamePlay findByGuid(String gamePlayGuid);
    GamePlay findByGuidAndRoxorStatus(String gamePlayGuid, GamePlay.RoxorStatus roxorStatus);
    List<GamePlay> findByGameId(Long gamePlayGuid);
    GamePlay findByCreatedDate(long createdDate);
    List<GamePlay> findByUserGuid(String guid);
    List<GamePlay> findByUserGuidAndCreatedDateGreaterThanEqualOrderByIdAsc(String guid, Long date);
 }
