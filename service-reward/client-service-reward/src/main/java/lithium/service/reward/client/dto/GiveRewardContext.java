package lithium.service.reward.client.dto;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GiveRewardContext implements Serializable {

  private ModelMapper modelMapper;

  @Default
  private StringBuilder log = new StringBuilder();
  private User player;
  private GiveRewardRequest giveRewardRequest;
  @Default
  private GiveRewardResponse giveRewardResponse = GiveRewardResponse.builder().build();

  private Long playerRewardTypeHistoryId;
  private Domain domain;
  private String locale;

  public List<RewardRevisionTypeValueOverride> findRewardRevisionTypeOverride(Long rewardRevisionTypeId) {
    if (giveRewardRequest.getRewardRevisionTypeValueOverrides() == null) return Collections.emptyList();
    return giveRewardRequest.getRewardRevisionTypeValueOverrides().stream().filter(rrt -> rrt.getRewardRevisionTypeId().equals(rewardRevisionTypeId)).collect(
        Collectors.toList());
  }
  public String playerGuid() {
    return giveRewardRequest.getPlayerGuid();
  }
  public String domainName() {
    return domain.getName();
  }

  public <D> D map(Object source, Class<D> destinationType) {
    return modelMapper.map(source, destinationType);
  }

  public void addLog(String log) {
    this.log.append(log).append(".");
  }

  public String compileLog() {
    return MessageFormat.format("GiveReward:: request: {0} response: {1}, player: {2}, context: {3}",
            getGiveRewardRequest(), getGiveRewardResponse(), getPlayer(), getLog().toString());
  }

  public String compileLog(String log) {
    addLog(log);
    return compileLog();
  }
}
