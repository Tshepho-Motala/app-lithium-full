package lithium.service.domain.client.objects.ecosystem;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
/**
 * Serves as a container entity for a group of domains that have certain relationships
 */
public class Ecosystem implements Serializable {

  private static final long serialVersionUID = -1;

  private Long id;
  private int version;
  private String name;
  private String displayName;
  private String description;
  private Boolean enabled;
  private Boolean deleted;
}
