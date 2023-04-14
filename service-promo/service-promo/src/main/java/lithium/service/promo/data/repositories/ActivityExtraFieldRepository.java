package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.Activity;
import lithium.service.promo.data.entities.ActivityExtraField;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityExtraFieldRepository extends PagingAndSortingRepository<ActivityExtraField, Long>,
    JpaSpecificationExecutor<ActivityExtraField> {

  ActivityExtraField findByActivityAndName(Activity activity, String name);
}
