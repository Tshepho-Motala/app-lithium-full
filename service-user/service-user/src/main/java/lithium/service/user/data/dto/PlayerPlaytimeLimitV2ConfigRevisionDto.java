package lithium.service.user.data.dto;

import java.time.ZoneOffset;
import lithium.service.user.data.entities.Granularity;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2ConfigRevision;
import lombok.Data;

@Data
public class PlayerPlaytimeLimitV2ConfigRevisionDto {
  private long id;
  private Granularity granularity;
  private long createdDate;
  private long effectiveFrom;
  private long secondsAllocated;

  public PlayerPlaytimeLimitV2ConfigRevisionDto(PlayerPlaytimeLimitV2ConfigRevision configRevision) {
    this.id = configRevision.getId();
    this.granularity = configRevision.getGranularity();
    this.createdDate = configRevision.getCreatedDate().toEpochSecond(ZoneOffset.UTC);
    this.effectiveFrom = configRevision.getEffectiveFrom().toEpochSecond(ZoneOffset.UTC);
    this.secondsAllocated = configRevision.getSecondsAllocated();
  }
}
