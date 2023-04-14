package lithium.service.reward.data.repositories;

import lithium.service.reward.data.entities.PlayerRewardTypeHistoryValue;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRewardTypeHistoryValueRepository extends JpaRepository<PlayerRewardTypeHistoryValue, Long> {
    List<PlayerRewardTypeHistoryValue> findAllByPlayerRewardTypeHistory(PlayerRewardTypeHistory playerRewardTypeHistory);
}
