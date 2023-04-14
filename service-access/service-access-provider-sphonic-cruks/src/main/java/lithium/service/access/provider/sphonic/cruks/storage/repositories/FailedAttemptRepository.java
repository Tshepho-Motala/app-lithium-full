package lithium.service.access.provider.sphonic.cruks.storage.repositories;

import lithium.service.access.provider.sphonic.cruks.storage.entities.FailedAttempt;
import lithium.service.access.provider.sphonic.cruks.storage.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface FailedAttemptRepository extends PagingAndSortingRepository<FailedAttempt, Long> {
	FailedAttempt findByUser(User user);
	Page<FailedAttempt> findAllByTotalAttemptsLessThanEqual(Long totalAttempts, Pageable pageable);
	FailedAttempt findByUserOrderByLastAttemptedAtAsc(User user);
	Optional<FailedAttempt> findByUserGuid(String userGuid);
}
