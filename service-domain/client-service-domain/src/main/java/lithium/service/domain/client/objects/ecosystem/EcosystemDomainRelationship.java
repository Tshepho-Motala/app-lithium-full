package lithium.service.domain.client.objects.ecosystem;

import java.io.Serializable;
import lithium.service.domain.client.objects.Domain;
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
 * Serves as a relationship definition rule for an ecosystem<br/>
 * The relationship is a singular attribute of a domain in relation to the other domains in the ecosystem<br/>
 * Eg. domain1 -> root; domain2 -> mutually_exclusive; domain3 -> member; domain4 -> mutually_exclusive<br/>
 */
public class EcosystemDomainRelationship implements Serializable {

  private static final long serialVersionUID = -1;

  private Long id;
  private int version;
  private Ecosystem ecosystem;
  private EcosystemRelationshipType relationship;
  private Domain domain;
  private Boolean enabled;
  private Boolean deleted;
  private Boolean disableRootWelcomeEmail;
}
