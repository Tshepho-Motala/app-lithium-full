package lithium.service.user.data.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlayerTagRequest {
  private String apiAuthorizationId;
  private String domainName;
  private String playerGuid;
  private List<Long> tagIds;
  private String timestamp;
  private String hash;

  public String payload() {
    StringBuilder payload = new StringBuilder();
    payload.append(this.apiAuthorizationId + "|");
    payload.append(this.domainName + "|");
    payload.append(this.playerGuid + "|");
    payload.append(this.tagIds + "|");
    payload.append(this.timestamp + "|");
    return payload.toString();
  }

}
