package lithium.service.domain.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueCode;
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
/**
 * List of possible relationships available for domains within an ecosystem.<br/>
 * Available relationships are persisted during module loading.
 */
public class EcosystemRelationshipType implements EntityWithUniqueCode, Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = true)
  private String description;

  @Builder.Default
  @Column(nullable = false)
  private Boolean enabled = true;

  @Builder.Default
  @Column(nullable = false)
  private Boolean deleted = false;

  //Adding this in since the builder defaults aren't triggering when using the creation factory (need to have a look in future)
  @PrePersist
  private void prePersist() {
    if (this.enabled == null) {
      this.enabled = true;
    }
    if (this.deleted == null) {
      this.deleted = false;
    }
  }
}
