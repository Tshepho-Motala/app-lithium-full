package lithium.service.stats.client.objects;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DomainStatSummary implements Serializable {

  private static final long serialVersionUID = -1L;

  private Long id;

  private DomainStat domainStat;

  private Period period;

  @Default
  private Long count = 0L;
}
