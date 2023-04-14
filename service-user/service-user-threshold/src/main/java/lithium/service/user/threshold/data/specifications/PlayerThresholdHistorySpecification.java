package lithium.service.user.threshold.data.specifications;

import java.util.Arrays;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryRequest;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.Domain_;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory_;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.service.user.threshold.data.entities.ThresholdRevision_;
import lithium.service.user.threshold.data.entities.Threshold_;
import lithium.service.user.threshold.data.entities.Type;
import lithium.service.user.threshold.data.entities.Type_;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.data.entities.User_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

@Slf4j
public class PlayerThresholdHistorySpecification {

  private PlayerThresholdHistorySpecification() {
  }

  public static Specification<PlayerThresholdHistory> findBy(PlayerThresholdHistoryRequest request, DataTableRequest tableRequest) {
    log.debug("Finding Threshold for: {} - {}", request, tableRequest); //Maybe we want to use search field one day?
    return (root, query, cb) -> {
      Predicate p = cb.conjunction();
      if (!ObjectUtils.isEmpty(request.getPlayerGuid())) {
        Join<PlayerThresholdHistory, User> userJoin = root.join(PlayerThresholdHistory_.USER, JoinType.INNER);
        p = cb.equal(userJoin.get(User_.GUID), request.getPlayerGuid());
      }

      Join<PlayerThresholdHistory, ThresholdRevision> revJoin = root.join(PlayerThresholdHistory_.THRESHOLD_REVISION, JoinType.INNER);
      Join<ThresholdRevision, Threshold> thresholdJoin = revJoin.join(ThresholdRevision_.THRESHOLD, JoinType.INNER);

      if (!ObjectUtils.isEmpty(request.getDomainName())) {
        Join<Threshold, Domain> domainJoin = thresholdJoin.join(Threshold_.DOMAIN, JoinType.INNER);
        p = cb.and(p, domainJoin.get(Domain_.name).in(Arrays.stream(request.getDomainName().split(",")).toList()));
      }

      if (!ObjectUtils.isEmpty(request.getGranularity())) {
        p = cb.and(p, cb.equal(thresholdJoin.get(Threshold_.GRANULARITY), Granularity.fromGranularity(request.getGranularity())));
      }
      if (!ObjectUtils.isEmpty(request.getTypeName())) {
        Join<Threshold, Type> typeJoin = thresholdJoin.join(Threshold_.TYPE, JoinType.INNER);
        p = cb.and(p, typeJoin.get(Type_.NAME).in(Arrays.stream(request.getTypeName()).toList()));
      }
      if (!ObjectUtils.isEmpty(request.getDateStart())) {
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(PlayerThresholdHistory_.THRESHOLD_HIT_DATE), request.getDateStart()));
      }
      if (!ObjectUtils.isEmpty(request.getDateEnd())) {
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(PlayerThresholdHistory_.THRESHOLD_HIT_DATE), request.getDateEnd()));
      }

      return p;
    };
  }
}
