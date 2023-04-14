package lithium.service.user.data.dto;

import java.time.ZoneOffset;
import lithium.service.user.data.entities.Period;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Entry;
import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
public class PlayerPlaytimeLimitV2EntryDto {

  private long id;
  private Period period;
  private long secondsAccumulated;
  private long limitReachedAt;

  public PlayerPlaytimeLimitV2EntryDto(PlayerPlaytimeLimitV2Entry entry) {
    if (!ObjectUtils.isEmpty(entry)) {
      this.id = entry.getId();
      this.period = entry.getPeriod();
      this.secondsAccumulated = entry.getSecondsAccumulated();
      if (!ObjectUtils.isEmpty(entry.getLimitReachedAt())) {
        this.limitReachedAt = entry.getLimitReachedAt().toEpochSecond(ZoneOffset.UTC);
      }
    }
  }
}
