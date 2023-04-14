package lithium.service.promo.pr.casino.roxor.dto;

import lithium.service.promo.client.dto.IActivity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Activity implements IActivity {
  WIN("win"),
  WAGER("wager");

  @Getter
  @Setter
  private String activity;

}
