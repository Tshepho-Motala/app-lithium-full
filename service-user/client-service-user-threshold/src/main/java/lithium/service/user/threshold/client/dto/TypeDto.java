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
public class TypeDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 4221527602854811074L;
  private String name;
}
