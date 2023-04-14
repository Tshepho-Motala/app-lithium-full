package lithium.service.user.data.dto;

import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Config;
import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
public class PlayerPlaytimeLimitV2ConfigDto {
  private long id;
  private PlayerPlaytimeLimitV2ConfigRevisionDto currentConfigRevision;
  private PlayerPlaytimeLimitV2ConfigRevisionDto pendingConfigRevision;

  public PlayerPlaytimeLimitV2ConfigDto(PlayerPlaytimeLimitV2Config config) {
    if(!ObjectUtils.isEmpty(config)){
      this.id = config.getId();
    }
    if(!ObjectUtils.isEmpty(config.getCurrentConfigRevision())){
      this.currentConfigRevision = new PlayerPlaytimeLimitV2ConfigRevisionDto(config.getCurrentConfigRevision());
    }
    if(!ObjectUtils.isEmpty(config.getPendingConfigRevision())){
      this.pendingConfigRevision = new PlayerPlaytimeLimitV2ConfigRevisionDto(config.getPendingConfigRevision());
    }
  }
}
