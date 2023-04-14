package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromotionStatEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PromotionStatEntryRepository extends PagingAndSortingRepository<PromotionStatEntry, Long> {
//	List<PromotionStatEntry> findByMissionStatAndOwner(PromotionStat promotionStat, User owner);
}