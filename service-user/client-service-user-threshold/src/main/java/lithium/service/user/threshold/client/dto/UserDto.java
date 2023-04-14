package lithium.service.user.threshold.client.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 7390477662114741981L;
  private String guid;
  private boolean testAccount;
  private Integer dobYear;
  private Integer dobMonth;
  private Integer dobDay;
  private boolean notifications;
  private DomainDto domain;
  private String username;

  private String accountCreationDate;

  public String getUserId() {
    return guid.replace(domain.getName()+"/", "");
  }
}
