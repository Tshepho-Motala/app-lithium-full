package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromotionRevision;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PromotionRevisionRepository extends PagingAndSortingRepository<PromotionRevision, Long>, JpaSpecificationExecutor<PromotionRevision> {
  default PromotionRevision findOne(Long id) {
    return findById(id).orElse(null);
  }

  @CacheEvict(value = "lithium.service.promo.services.promotion-service.all-current-enabled-with-events-by-domain", key="#entity.promotion.cacheKey")
  @Override
  <S extends PromotionRevision> S save(S entity);
}
