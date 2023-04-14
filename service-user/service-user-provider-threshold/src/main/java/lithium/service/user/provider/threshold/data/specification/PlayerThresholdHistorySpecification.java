package lithium.service.user.provider.threshold.data.specification;

import java.util.Date;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.Domain_;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory_;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision_;
import org.springframework.data.jpa.domain.Specification;

public class PlayerThresholdHistorySpecification {
  public static Specification<PlayerThresholdHistory> domainIn(final String[] domains) {
    return (root, query, cb) -> {
      Join<PlayerThresholdHistory, ThresholdRevision> joinRevision = root.join(PlayerThresholdHistory_.thresholdRevision, JoinType.INNER);
      Join<ThresholdRevision, Domain> joinDomain = joinRevision.join(ThresholdRevision_.domain, JoinType.LEFT);
      return joinDomain.get(Domain_.name).in(domains);
    };
  }

  public static Specification<PlayerThresholdHistory> createdDateBetween(final Date start, final Date end) {
    return (root, query, cb) -> cb.between(root.get(PlayerThresholdHistory_.thresholdHitDate), start, end);
  }
}
