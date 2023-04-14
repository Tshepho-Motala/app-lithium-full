package lithium.service.domain.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(indexes = {
    @Index(name="idx_edr_all", columnList="ecosystem_id, domain_id, relationship_id", unique=true)
})
/**
 * Serves as a relationship definition library for an ecosystem<br/>
 * The relationship is a singular attribute of a domain in relation to the other domains in the ecosystem<br/>
 * Eg. domain1 -> root; domain2 -> mutually_exclusive; domain3 -> member; domain4 -> mutually_exclusive<br/>
 */
public class EcosystemDomainRelationship implements Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn
  private Ecosystem ecosystem;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn
  private EcosystemRelationshipType relationship;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn
  private Domain domain;

  @Builder.Default
  @Column(nullable = false)
  private Boolean enabled = true;

  @Builder.Default
  @Column(nullable = false)
  private Boolean deleted = false;

  @Builder.Default
  @Column(nullable = false)
  private Boolean disableRootWelcomeEmail = false;
}
