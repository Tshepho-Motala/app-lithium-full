package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserNotification {

  private String userGuid;
  private String notificationName;
  private boolean cta;
  private List<InboxMessagePlaceholderReplacement> phReplacements;
  private Map<String, String> metaData;
}
