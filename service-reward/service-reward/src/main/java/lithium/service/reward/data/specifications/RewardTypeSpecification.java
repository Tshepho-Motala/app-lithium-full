package lithium.service.reward.data.specifications;

import lithium.service.reward.data.entities.RewardType;
import lithium.service.reward.data.entities.RewardType_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RewardTypeSpecification {

    public static Specification<RewardType> forProviders(List<String> providerGuids)  {
        return ((root, query, criteriaBuilder) -> root.get(RewardType_.URL).in(providerGuids));
    }
}
