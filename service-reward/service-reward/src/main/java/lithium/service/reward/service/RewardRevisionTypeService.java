package lithium.service.reward.service;

import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.repositories.RewardRevisionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardRevisionTypeService {

    private final RewardRevisionTypeRepository rewardRevisionTypeRepository;

    public RewardRevisionType save(RewardRevisionType rewardRevisionType) {
        return rewardRevisionTypeRepository.save(rewardRevisionType);
    }

    public RewardRevisionType saveOrUpdate(RewardRevisionType rewardRevisionType) {

        Optional<RewardRevisionType> results = rewardRevisionTypeRepository.findByRewardRevisionAndRewardType(rewardRevisionType.getRewardRevision(), rewardRevisionType.getRewardType());

        if(results.isPresent()) {
            return results.get();
        }

        return rewardRevisionTypeRepository.save(rewardRevisionType);
    }

    public List<RewardRevisionType> findByRewardRevision(RewardRevision rewardRevision) {
        return rewardRevisionTypeRepository.findByRewardRevision(rewardRevision);
    }
}
