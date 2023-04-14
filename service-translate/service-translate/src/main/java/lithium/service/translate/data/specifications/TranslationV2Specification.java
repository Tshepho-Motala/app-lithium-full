package lithium.service.translate.data.specifications;

import lithium.service.translate.data.entities.Domain_;
import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.entities.TranslationKeyV2_;
import lithium.service.translate.data.entities.TranslationValueV2;
import lithium.service.translate.data.entities.TranslationValueV2_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;

public class TranslationV2Specification {

    public static Specification<TranslationKeyV2> startingWith(String value) {
        return (root, query, cb) -> {
            Predicate p = cb.like(root.get(TranslationKeyV2_.code), value + "%");
            query.distinct(true);
            return p;
        };
    }

    public static Specification<TranslationKeyV2> keysForDomainOrDefault(String domain) {
        return (root, query, cb) -> {
            ListJoin<TranslationKeyV2, TranslationValueV2> join = root.joinList("values", JoinType.INNER);

            return cb.or(cb.equal(join.get(TranslationValueV2_.domain).get(Domain_.name), domain),
                    cb.equal(join.get(TranslationValueV2_.domain).get(Domain_.name), "default"));
        };
    }
}