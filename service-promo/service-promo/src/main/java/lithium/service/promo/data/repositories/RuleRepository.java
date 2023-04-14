package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.Rule;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RuleRepository extends PagingAndSortingRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {
//	List<Rule> findByChallenge(Challenge challenge);
    //	List<Rule> findByTypeAndActionAndIdentifier(String type, String action, String identifier);


    @CacheEvict(value = "lithium.service.promo.data.repositories.activity-extra-field-rule-value-repository.find-by-rule", key = "#entity.id")
    @Override
    <S extends Rule> S save(S entity);
}
