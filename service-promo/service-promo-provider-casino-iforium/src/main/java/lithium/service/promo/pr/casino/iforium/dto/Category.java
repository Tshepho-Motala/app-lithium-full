package lithium.service.promo.pr.casino.iforium.dto;

import lithium.service.promo.client.dto.ICategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Category implements ICategory {
  CASINO("casino");

  @Setter
  @Getter
  private String category;

}