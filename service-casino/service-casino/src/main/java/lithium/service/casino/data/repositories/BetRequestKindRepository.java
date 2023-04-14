package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.data.entities.BetRequestKind;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRequestKindRepository extends FindOrCreateByCodeRepository<BetRequestKind, Long> {
	@Cacheable(value = "lithium.service.casino.entities.betrequestkind.byCode", unless = "#result == null")
    BetRequestKind findByCode(String code);
}