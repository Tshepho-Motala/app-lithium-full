package lithium.service.limit.client.objects;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLimitSummary {
   private BigDecimal balanceLimit;
   private BigDecimal depositLimitDay;
   private BigDecimal depositLimitWeek;
   private BigDecimal depositLimitMonth;
   private long playTimeLimitInMinutes;
   private long playTimeLimitSeconds;
   private long playTimeLimitRemainingSeconds;
   private String playTimeLimitGranularity;
   private String timeSlotLimitStart;
   private String timeSlotLimitEnd;
}
