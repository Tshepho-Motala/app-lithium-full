package lithium.service.casino.mock.all.repositories;

import lithium.service.casino.mock.all.entities.MockSession;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface MockSessionRepository extends PagingAndSortingRepository<MockSession, Long> {
	MockSession findByAuthTokenAndGameStartUrl(final String authToken, final String gameStartUrl);
	Optional<MockSession> findById(final long id);
}