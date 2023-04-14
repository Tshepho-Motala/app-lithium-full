package lithium.service.promo.data.repositories;

import java.util.List;
import lithium.service.promo.data.entities.ActivityExtraFieldRuleValue;
import lithium.service.promo.data.entities.Rule;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityExtraFieldRuleValueRepository extends PagingAndSortingRepository<ActivityExtraFieldRuleValue, Long>,
        JpaSpecificationExecutor<ActivityExtraFieldRuleValue> {

  @Cacheable(value="lithium.service.promo.data.repositories.activity-extra-field-rule-value-repository.find-by-rule", key = "#rule.id", unless="#result == null or #result.size() == 0")
  List<ActivityExtraFieldRuleValue> findByRule(Rule rule);
}
