package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.entity.EntityFactory;
import lithium.service.casino.provider.roxor.storage.entities.RewardBonusMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardBonusMapRepository extends JpaRepository<RewardBonusMap, Long> {

    RewardBonusMap findByLithiumExtBonusId(Long lithiumExtBonusId);

    RewardBonusMap findByRoxorRewardId(String roxorRewardId);

    default public RewardBonusMap findOrCreateByLithiumExtBonusId(Long lithiumExtBonusId, EntityFactory<RewardBonusMap> factory) {
        RewardBonusMap t = findByLithiumExtBonusId(lithiumExtBonusId);
        if (t == null) {
            t = factory.build();
            t.setLithiumExtBonusId(lithiumExtBonusId);
            save(t);
        }
        return t;
    }
}
