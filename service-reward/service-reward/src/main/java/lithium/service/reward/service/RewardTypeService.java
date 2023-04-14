package lithium.service.reward.service;

import java.util.ArrayList;
import java.util.List;

import lithium.service.reward.data.entities.RewardType;
import lithium.service.reward.data.entities.RewardTypeField;
import lithium.service.reward.data.repositories.RewardTypeFieldRepository;
import lithium.service.reward.data.repositories.RewardTypeRepository;
import lithium.service.reward.data.specifications.RewardTypeSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RewardTypeService {

  @Autowired
  RewardTypeRepository rewardTypeRepository;
  @Autowired
  RewardTypeFieldRepository rewardTypeFieldRepository;

  private RewardType findOrCreate(String url, String name) {
    RewardType rewardType = rewardTypeRepository.findByUrlAndName(url, name);
    if (rewardType == null) {
      rewardType = rewardTypeRepository.save(RewardType.builder().name(name).url(url).build());
    }
    return rewardType;
  }

  private RewardTypeField findOrCreate(RewardType rewardType, String name) {
    RewardTypeField rewardTypeField = rewardTypeFieldRepository.findByRewardTypeAndName(rewardType, name);
    if (rewardTypeField == null) {
      rewardTypeField = rewardTypeFieldRepository.save(RewardTypeField.builder().name(name).rewardType(rewardType).build());
    }
    return rewardTypeField;
  }

  public List<RewardType> getTypesForProviders(List<String> providerGuids) {
    Specification<RewardType> querySpec = RewardTypeSpecification.forProviders(providerGuids);
    return rewardTypeRepository.findAll(querySpec);
  }

  public void register(lithium.service.reward.client.dto.RewardType request) {
    log.debug("Registering new Reward Type: " + request);

    RewardType rewardType = findOrCreate(request.getUrl(), request.getName());

    List<RewardTypeField> rewardTypeFields = new ArrayList<>();
    if (request.getSetupFields() != null) {
      for (lithium.service.reward.client.dto.RewardTypeField rtf: request.getSetupFields()) {
        RewardTypeField rewardTypeField = findOrCreate(rewardType, rtf.getName());
        rewardTypeField.setDataType(rtf.getDataType());
        rewardTypeField.setDescription(rtf.getDescription());
        rewardTypeFields.add(rewardTypeField);
      }
    }
    rewardType.setSetupFields(rewardTypeFields);
    rewardType.setDisplayGames(request.isDisplayGames());
    rewardType.setCode(request.getCode());
    rewardTypeRepository.save(rewardType);
  }

  public RewardType findByUrlAndName(String url, String name) {
    return rewardTypeRepository.findByUrlAndName(url, name);
  }
}
