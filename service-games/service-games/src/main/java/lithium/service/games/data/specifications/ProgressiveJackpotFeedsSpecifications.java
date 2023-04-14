package lithium.service.games.data.specifications;

import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ProgressiveJackpotFeedsSpecifications {

    public static Specification<ProgressiveJackpotGameBalance> any(final String domainName) {
        return (root, query, cb) -> {
            Predicate p = cb.equal(root.get(ProgressiveJackpotGameBalance_.game), domainName + "%");
            return p;
        };
    }
}
