package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromoProvider;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PromoProviderRepository extends PagingAndSortingRepository<PromoProvider, Long>, JpaSpecificationExecutor<PromoProvider> {
  PromoProvider findByUrlAndCategoryName(String url, String category);
  List<PromoProvider> findByCategoryName(String category);
}
