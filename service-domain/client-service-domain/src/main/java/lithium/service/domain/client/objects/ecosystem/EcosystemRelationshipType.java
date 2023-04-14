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
 * List of possible relationships available for domains within an ecosystem.<br/>
 * Available relationships are persisted during module loading.
 */
public class EcosystemRelationshipType implements Serializable {

  private static final long serialVersionUID = -1;

  private Long id;
  private int version;
  private String code;
  private String description;
  private Boolean enabled;
  private Boolean deleted;
}
