package lithium.service.access.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleMessageUpdate {
  private String type;
  private String message;
  private String timeoutMessage;
  private String reviewMessage;
}
