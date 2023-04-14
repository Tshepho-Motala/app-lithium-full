package lithium.service.reward.data.specifications;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.entities.Domain_;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.PlayerRewardHistory_;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevision_;
import lithium.service.reward.data.entities.Reward_;
import lithium.service.reward.data.entities.User;
import lithium.service.reward.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

public class PlayerRewardHistorySpecification {

  private PlayerRewardHistorySpecification() {
  }

  public static Specification<PlayerRewardHistory> statusIn(List<PlayerRewardHistoryStatus> statuses) {
    return ((root, query, criteriaBuilder) -> root.get(PlayerRewardHistory_.status).in(statuses));
  }

  public static Specification<PlayerRewardHistory> player(String playerGuid) {
    return (root, query, cb) -> {
      Join<PlayerRewardHistory, User> joinUser = root.join(PlayerRewardHistory_.player, JoinType.INNER);
      return cb.equal(joinUser.get(User_.guid), playerGuid);
    };
  }

  public static Specification<PlayerRewardHistory> domain(String domainName) {
    return (root, query, cb) -> cb.equal(joinDomain(root).get(Domain_.NAME), domainName);
  }

  public static Specification<PlayerRewardHistory> domainIn(List<String> domainNames) {
    return (root, query, cb) -> {
      Join<Reward, Domain> joinDomain = joinDomain(root);
      return joinDomain.get(Domain_.NAME).in(domainNames);
    };
  }

  public static Specification<PlayerRewardHistory> historyStatuses(String[] historyStatuses) {
    return (root, query, cb) -> root.get(PlayerRewardHistory_.STATUS)
        .in(Arrays.stream(historyStatuses).map(PlayerRewardHistoryStatus::fromStatus).toList());
  }

  public static Specification<PlayerRewardHistory> rewardCode(String rewardCode) {
    return (root, query, cb) -> {
      Join<PlayerRewardHistory, RewardRevision> joinRewardRevision = joinRewardRevision(root);
      return cb.equal(joinRewardRevision.get(RewardRevision_.CODE), rewardCode);
    };
  }

  public static Specification<PlayerRewardHistory> rewardId(Long rewardId) {
    return (root, query, cb) -> {
      Join<PlayerRewardHistory, RewardRevision> joinRewardRevision = joinRewardRevision(root);
      return cb.equal(joinRewardRevision.get(RewardRevision_.ID), rewardId);
    };
  }

  public static Specification<PlayerRewardHistory> awardedDateFrom(Date awardedDateFrom) {
    return (root, query, cb) -> cb.greaterThan(root.get(PlayerRewardHistory_.AWARDED_DATE), awardedDateFrom);
  }

  public static Specification<PlayerRewardHistory> awardedDateTo(Date awardedDateTo) {
    return (root, query, cb) -> cb.lessThan(root.get(PlayerRewardHistory_.AWARDED_DATE), awardedDateTo);
  }

  public static Specification<PlayerRewardHistory> redeemedDateFrom(Date redeemedDateFrom) {
    return (root, query, cb) -> cb.greaterThan(root.get(PlayerRewardHistory_.REDEEMED_DATE), redeemedDateFrom);
  }

  public static Specification<PlayerRewardHistory> redeemedDateTo(Date redeemedDateTo) {
    return (root, query, cb) -> cb.lessThan(root.get(PlayerRewardHistory_.REDEEMED_DATE), redeemedDateTo);
  }

  public static Specification<PlayerRewardHistory> expiryDateFrom(Date expiryDateFrom) {
    return (root, query, cb) -> cb.greaterThan(root.get(PlayerRewardHistory_.EXPIRY_DATE), expiryDateFrom);
  }

  public static Specification<PlayerRewardHistory> expiryDateTo(Date expiryDateTo) {
    return (root, query, cb) -> cb.lessThan(root.get(PlayerRewardHistory_.EXPIRY_DATE), expiryDateTo);
  }

  private static Join<PlayerRewardHistory, RewardRevision> joinRewardRevision(Root<PlayerRewardHistory> root) {
    return root.join(PlayerRewardHistory_.rewardRevision, JoinType.INNER);
  }

  private static Join<Reward, Domain> joinDomain(Root<PlayerRewardHistory> root) {
    Join<PlayerRewardHistory, RewardRevision> joinRewardRevision = joinRewardRevision(root);
    Join<RewardRevision, Reward> joinReward = joinRewardRevision.join(RewardRevision_.reward, JoinType.INNER);
    return joinReward.join(Reward_.domain, JoinType.INNER);
  }
}
