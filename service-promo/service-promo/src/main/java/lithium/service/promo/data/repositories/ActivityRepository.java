package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.Activity;
import lithium.service.promo.data.entities.PromoProvider;
import lithium.service.promo.data.projections.PromoActivityProjection;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
  List<PromoActivityProjection> findByPromoProviderCategoryName(String name);
  Activity findByPromoProviderAndName(PromoProvider promoProvider, String name);
}
