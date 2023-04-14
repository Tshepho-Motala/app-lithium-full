package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.slotapi.storage.entities.BetRequestKind;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import org.springframework.cache.annotation.Cacheable;

public interface BetRequestKindRepository extends FindOrCreateByCodeRepository<BetRequestKind, Long> {

	@Cacheable(value = "lithium.service.casino.provider.slotapi.storage.entities.betrequestkind.byCode", unless = "#result == null")
    BetRequestKind findByCode(String code);

}