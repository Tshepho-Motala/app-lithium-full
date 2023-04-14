package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.data.entities.BetResultKind;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetResultKindRepository extends FindOrCreateByCodeRepository<BetResultKind, Long> {
	@Cacheable(value = "lithium.service.casino.entities.betresultkind.byCode", unless = "#result == null")
    BetResultKind findByCode(String code);

	Iterable<BetResultKind> findAllByIdIn(List<Long> ids);
}
