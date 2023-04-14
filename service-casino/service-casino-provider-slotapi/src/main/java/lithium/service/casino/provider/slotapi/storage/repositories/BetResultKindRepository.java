package lithium.service.casino.provider.slotapi.storage.repositories;

import java.util.List;
import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 */
public interface BetResultKindRepository extends FindOrCreateByCodeRepository<BetResultKind, Long> {

	@Cacheable(value = "lithium.service.casino.provider.slotapi.storage.entities.betresultkind.byCode", unless = "#result == null")
  BetResultKind findByCode(String code);

	Iterable<BetResultKind> findAllByIdIn(List<Long> ids);
}
