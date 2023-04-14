package lithium.service.cdn.cms.data.specifications;

import lithium.service.cdn.cms.data.entities.CmsAsset;
import lithium.service.cdn.cms.data.entities.CmsAsset_;
import lithium.service.cdn.cms.data.entities.Domain;
import lithium.service.cdn.cms.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class CmsAssetSpecification {
  public static Specification<CmsAsset> domainName(String domainName) {
    return (root, query, cb) -> {
      Join<CmsAsset, Domain> joinDomain = root.join(CmsAsset_.domain, JoinType.INNER);
      Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
      return p;
    };
  }

  public static Specification<CmsAsset> isDeleted(boolean isDeleted) {
    return (root, query, cb) -> cb.equal(root.get(CmsAsset_.deleted), isDeleted);
  }

  public static Specification<CmsAsset> type(String type) {
    return (root,query,cb) -> cb.equal(root.get(CmsAsset_.type), type);
  }

  public static Specification<CmsAsset> any(String value) {
    return (root, query, cb) -> {
      Predicate p = cb.like(cb.upper(root.get(CmsAsset_.name)), value.toUpperCase() + "%");
      return p;
    };
  }
}
