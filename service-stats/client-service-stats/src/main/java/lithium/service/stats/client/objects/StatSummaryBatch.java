package lithium.service.stats.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class StatSummaryBatch implements Serializable {

  @Serial
  private static final long serialVersionUID = -7894836502572439490L;
  
  private String eventName;
  private List<StatSummary> statSummaries;
  private List<DomainStatSummary> domainStatSummaries;
}