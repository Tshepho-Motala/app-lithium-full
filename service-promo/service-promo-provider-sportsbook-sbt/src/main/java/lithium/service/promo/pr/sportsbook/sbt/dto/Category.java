package lithium.service.promo.pr.sportsbook.sbt.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.promo.client.dto.ICategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum Category implements ICategory {
  SPORT("sport");

  @Getter
  @Setter
  private String category;

  @JsonCreator
  public static Category fromCategory(String category) {
    for (Category g: Category.values()) {
      if (g.category.equalsIgnoreCase(category)) {
        return g;
      }
    }
    return null;
  }
}