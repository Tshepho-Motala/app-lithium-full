package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.UserCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserCategoryRepository extends PagingAndSortingRepository<UserCategory, Long>, JpaSpecificationExecutor<UserCategory> {
}
