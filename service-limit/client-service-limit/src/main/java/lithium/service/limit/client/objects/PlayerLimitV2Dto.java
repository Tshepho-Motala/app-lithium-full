package lithium.service.limit.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class PlayerLimitV2Dto implements Serializable {

  @Serial
  private static final long serialVersionUID = -7543334911265060190L;
  private String playerGuid;
  private String domainName;
  private int type;
  private int granularity;
  private BigDecimal limitAmount;
  private BigDecimal netLossAmount;
}
