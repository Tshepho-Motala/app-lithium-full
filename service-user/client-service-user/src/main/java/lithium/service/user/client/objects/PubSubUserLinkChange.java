package lithium.service.user.client.objects;

import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubObj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubUserLinkChange  implements PubSubObj {
  private PubSubEventType eventType;
  private String accountIdA;
  private String accountIdB;
  private String linkType;
  private String reason;
}
