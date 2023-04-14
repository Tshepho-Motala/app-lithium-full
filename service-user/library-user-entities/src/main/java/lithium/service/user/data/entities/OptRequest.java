package lithium.service.user.data.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptRequest {

  private String method;
  private boolean optOut;
  private String guid;
  private String description;
}
