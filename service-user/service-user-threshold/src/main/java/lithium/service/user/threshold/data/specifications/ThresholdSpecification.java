package lithium.service.user.threshold.data.specifications;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.Domain_;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.Threshold_;
import lithium.service.user.threshold.data.entities.Type;
import lithium.service.user.threshold.data.entities.Type_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class ThresholdSpecification {

  private ThresholdSpecification() {
  }

  public static Specification<Threshold> findByAge(String domainName, EType eType, Integer granularity, Integer age) {
    log.trace("Finding Threshold (Age) for: domain: {} - type: {} - granularity: {} - age: {}", domainName, eType.name(),
        Granularity.fromGranularity(granularity).type(), age);
    return (root, query, cb) -> {
      Join<Threshold, Domain> domainJoin = root.join(Threshold_.DOMAIN, JoinType.INNER);
      Join<Threshold, Type> typeJoin = root.join(Threshold_.TYPE, JoinType.INNER);
      return cb.and(cb.equal(domainJoin.get(Domain_.NAME), domainName), cb.equal(typeJoin.get(Type_.NAME), eType.name()),
          cb.equal(root.get(Threshold_.GRANULARITY), Granularity.fromGranularity(granularity)), cb.isTrue(root.get(Threshold_.ACTIVE)),
          cb.and(cb.lessThanOrEqualTo(root.get(Threshold_.AGE_MIN), age), cb.greaterThanOrEqualTo(root.get(Threshold_.AGE_MAX), age)));
    };
  }

  public static Specification<Threshold> findDefault(String domainName, EType eType, Integer granularity) {
    log.trace("Finding Threshold (Default) for: domain: {} - type: {} - granularity: {}", domainName, eType.name(),
        Granularity.fromGranularity(granularity).type());
    return (root, query, cb) -> {
      Join<Threshold, Domain> domainJoin = root.join(Threshold_.DOMAIN, JoinType.INNER);
      Join<Threshold, Type> typeJoin = root.join(Threshold_.TYPE, JoinType.INNER);
      return cb.and(cb.equal(domainJoin.get(Domain_.NAME), domainName), cb.equal(typeJoin.get(Type_.NAME), eType.name()),
          cb.equal(root.get(Threshold_.GRANULARITY), Granularity.fromGranularity(granularity)), cb.isTrue(root.get(Threshold_.ACTIVE)),
          cb.and(cb.equal(root.get(Threshold_.AGE_MIN), -1), cb.equal(root.get(Threshold_.AGE_MAX), -1)));
    };
  }
}
