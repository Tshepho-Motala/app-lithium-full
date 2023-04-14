package lithium.service.promo.pr.user.dto;

import lithium.service.promo.client.dto.ICategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Category implements ICategory {
  USER("user");

  @Setter
  @Getter
  private String category;

}