package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.service.casino.provider.roxor.storage.entities.GamePlayRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamePlayRequestRepository extends JpaRepository<GamePlayRequest, Long> {
}
