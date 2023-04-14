package lithium.service.promo.stubs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.promo.client.dto.IActivity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ActivityStub implements IActivity {
  WAGER("wager"),
  WIN("win"),
  BET("bet"),
  LOGIN_SUCCESS("login-success");

  @Getter
  @Setter
  private String activity;
}
