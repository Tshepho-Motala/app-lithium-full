package lithium.service.reward.mappers;

import lithium.service.reward.data.entities.RewardRevision;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RewardBOMapper {

  public lithium.service.reward.client.dto.Reward convertToRewardBO(lithium.service.reward.data.entities.Reward reward){
    return lithium.service.reward.client.dto.Reward.builder()
          .id(reward.getId())
          .editUser(lithium.service.reward.client.dto.User.builder()
                .id(reward.getEditUser().getId())
                .apiToken(reward.getEditUser().getApiToken())
                .guid(reward.getEditUser().guid())
                .isTestAccount(reward.getEditUser().isTestAccount())
                .originalId(reward.getEditUser().getOriginalId())
                .build())
          .domain(lithium.service.reward.client.dto.Domain.builder()
                .id(reward.getDomain().getId())
                .name(reward.getDomain().getName())
                .build())
          .current(convertToRewardRevisionBO(reward.getCurrent()))
          .build();
  }

  public lithium.service.reward.client.dto.RewardRevision convertToRewardRevisionBO(RewardRevision rewardRevision){
    return lithium.service.reward.client.dto.RewardRevision.builder()
          .id(rewardRevision.getId())
          .name(rewardRevision.getName())
          .code(rewardRevision.getCode())
          .description(rewardRevision.getDescription())
          .activationNotificationName(rewardRevision.getActivationNotificationName())
          .enabled(rewardRevision.isEnabled())
          .validFor(rewardRevision.getValidFor())
          .validForGranularity(rewardRevision.getValidForGranularity())
          .revisionTypes(rewardRevision
                .getRevisionTypes()
                .stream()
                .map(rewardRevisionType -> lithium.service.reward.client.dto.RewardRevisionType.builder()
                      .id(rewardRevisionType.getId())
                      .instant(rewardRevisionType.isInstant())
                      .rewardType(convertToRewardTypeBO(rewardRevisionType.getRewardType()))
                      .build())
                .collect(Collectors.toList()))
          .build();
  }

  public lithium.service.reward.client.dto.RewardType convertToRewardTypeBO(lithium.service.reward.data.entities.RewardType rewardType) {
    return lithium.service.reward.client.dto.RewardType.builder()
          .id(rewardType.getId())
          .code(rewardType.getCode())
          .name(rewardType.getName())
          .url(rewardType.getUrl())
          .displayGames(rewardType.isDisplayGames())
          .setupFields(rewardType.getSetupFields()
                .stream()
                .map(setupField -> lithium.service.reward.client.dto.RewardTypeField.builder()
                      .id(setupField.getId())
                      .name(setupField.getName())
                      .description(setupField.getDescription())
                      .dataType(setupField.getDataType())
                      .build()).collect(Collectors.toList()))
          .build();
  }

  public lithium.service.reward.client.dto.RewardRevisionTypeGame convertToRewardRevisionTypeGame(lithium.service.reward.data.entities.RewardRevisionTypeGame rewardRevisionTypeGame){
    return lithium.service.reward.client.dto.RewardRevisionTypeGame.builder()
          .id(rewardRevisionTypeGame.getId())
          .gameId(rewardRevisionTypeGame.getGameId())
          .gameName(rewardRevisionTypeGame.getGameName())
          .guid(rewardRevisionTypeGame.getGuid())
          .build();
  }
}
