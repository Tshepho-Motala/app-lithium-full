package lithium.service.user.data.entities;

import lombok.Data;

@Data
public class UserGuidOnlyDTO {
  private Long id;
  private String guid;
  private String username;
}
