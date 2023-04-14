package lithium.service.casino.search.data.repositories.casino;

import lithium.service.casino.data.entities.BetResultKind;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("casino.BetResultKindRepository")
public interface BetResultKindRepository extends PagingAndSortingRepository<BetResultKind, Long> {
}
