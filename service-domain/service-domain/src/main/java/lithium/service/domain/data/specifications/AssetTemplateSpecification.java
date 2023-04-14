package lithium.service.domain.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.domain.data.entities.AssetTemplate;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Domain_;
import lithium.service.domain.data.entities.AssetTemplate_;

public class AssetTemplateSpecification {
  public static Specification<AssetTemplate> domainName(String domainName) {
    return (root, query, cb) -> {
      Join<AssetTemplate, Domain> joinDomain = root.join(AssetTemplate_.domain, JoinType.INNER);
      Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
      return p;
    };
  }

  public static Specification<AssetTemplate> isDeleted(boolean isDeleted) {
    return (root, query, cb) -> cb.equal(root.get(AssetTemplate_.deleted), isDeleted);
  }

  public static Specification<AssetTemplate> any(String value) {
    return (root, query, cb) -> {
      Predicate p = cb.like(cb.upper(root.get(AssetTemplate_.name)), value.toUpperCase() + "%");
      p = cb.or(p, cb.like(cb.upper(root.get(AssetTemplate_.description)), value.toUpperCase() + "%"));
      return p;
    };
  }
}
