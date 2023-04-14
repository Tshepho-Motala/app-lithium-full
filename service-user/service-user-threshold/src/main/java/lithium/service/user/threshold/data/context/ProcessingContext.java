package lithium.service.user.threshold.data.context;

import java.io.Serial;
import java.io.Serializable;
import lithium.service.accounting.objects.Period;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.user.threshold.client.enums.LossLimitVisibilityMessageType;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingContext implements Serializable {

  @Serial
  private static final long serialVersionUID = -5247121421392807737L;
  private Domain domain;
  private String defaultDomainCurrencySymbol;
  private User user;
  private int playerAge;
  private Threshold threshold;
  private Period period;
  private PlayerLimitV2Dto limit;
  private PlayerThresholdHistory playerThresholdHistory;

  private LossLimitVisibilityMessageType messageType;
}
