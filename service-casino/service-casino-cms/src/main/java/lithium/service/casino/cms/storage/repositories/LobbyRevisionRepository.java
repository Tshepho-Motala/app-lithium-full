package lithium.service.casino.cms.storage.repositories;

import lithium.service.casino.cms.storage.entities.Lobby;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LobbyRevisionRepository extends PagingAndSortingRepository<LobbyRevision, Long>, JpaSpecificationExecutor<LobbyRevision> {
	Page<LobbyRevision> findByLobby(Lobby lobby, Pageable pageable);
}
