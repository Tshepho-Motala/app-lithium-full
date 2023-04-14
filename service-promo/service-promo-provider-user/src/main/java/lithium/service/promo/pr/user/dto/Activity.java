package lithium.service.promo.pr.user.dto;

import lithium.service.promo.client.dto.IActivity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Activity implements IActivity {
  LOGIN("login-success"),
  REGISTRATION("registration-success");

  @Getter
  @Setter
  private String activity;

}
