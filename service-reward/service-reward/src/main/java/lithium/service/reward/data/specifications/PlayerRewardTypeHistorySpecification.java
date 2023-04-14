package lithium.service.reward.data.specifications;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.PlayerRewardHistory_;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory_;
import lithium.service.reward.data.entities.User;
import lithium.service.reward.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PlayerRewardTypeHistorySpecification {

    public static Specification<PlayerRewardTypeHistory> withStatuses(List<PlayerRewardComponentStatus> statuses) {
        return ((root, query, criteriaBuilder) -> root.get(PlayerRewardTypeHistory_.status).in(statuses));
    }

    public static Specification<PlayerRewardTypeHistory> player(String playerGuid) {
        return (root, query, cb) -> {
            Join<PlayerRewardTypeHistory, PlayerRewardHistory> joinHistory = root.join(PlayerRewardTypeHistory_.playerRewardHistory, JoinType.INNER);
            Join<PlayerRewardHistory, User> joinUser = joinHistory.join(PlayerRewardHistory_.player, JoinType.INNER);
            Predicate p = cb.equal(joinUser.get(User_.guid), playerGuid);
            return p;
        };
    }
}
