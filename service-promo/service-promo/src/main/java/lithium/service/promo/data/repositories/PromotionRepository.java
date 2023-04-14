package lithium.service.promo.data.repositories;

import java.util.Date;
import java.util.List;
import lithium.service.promo.data.entities.Promotion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PromotionRepository extends PagingAndSortingRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {

  List<Promotion> findByCurrentDomainNameAndCurrentEndDateGreaterThanOrCurrentEndDateIsNullAndCurrentStartDateLessThanEqualOrCurrentStartDateIsNull(
          String domainName, Date now, Date now2);

  default Promotion findOne(Long id) {
    return findById(id).orElse(null);
  }

  List<Promotion> findByCurrentDomainName(String domainName);

  List<Promotion> findByCurrentRewardRewardId(Long rewardId);

  @CacheEvict(value = "lithium.service.promo.services.promotion-service.all-current-enabled-with-events-by-domain", key="#entity.cacheKey")
  @Override
  <S extends Promotion> S save(S entity);
}
 