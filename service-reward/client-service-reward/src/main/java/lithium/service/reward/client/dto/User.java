package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

  private static final long serialVersionUID = -3317799579159948891L;
  private Long id;
  private String guid;
  private String apiToken;
  private String originalId; //from svc-user
  private boolean isTestAccount;

  /// Utility methods
  public String domainName() {
    return guid.split("/")[0];
  }

  public String username() {
    return guid.split("/")[1];
  }

  public String guid() {
    return guid;
  }
}
