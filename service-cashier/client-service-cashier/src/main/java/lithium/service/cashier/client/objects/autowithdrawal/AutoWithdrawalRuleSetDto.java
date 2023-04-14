package lithium.service.cashier.client.objects.autowithdrawal;

import lithium.service.cashier.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AutoWithdrawalRuleSetDto {

  private Long id;

  private int version;
  private Domain domain;
  private String name;
  private boolean enabled;
  private boolean deleted;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date lastUpdated;
  private String lastUpdatedBy;
  private Long delay;
  private boolean delayedStart;
  private List<AutoWithdrawalRuleDto> rules;
}
