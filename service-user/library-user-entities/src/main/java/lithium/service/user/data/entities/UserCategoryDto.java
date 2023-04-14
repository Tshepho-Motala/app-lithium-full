package lithium.service.user.data.entities;

import java.util.List;
import lombok.Data;

@Data
public class UserCategoryDto {
  private Long id;
  private String name;
  private String description;
  private Boolean dwhVisible;
  private Domain domain;
  private List<UserGuidOnlyDTO> users;
}
