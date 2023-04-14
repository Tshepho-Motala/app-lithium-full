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
public class DomainDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 3701118158557873232L;
  private String name;
}
