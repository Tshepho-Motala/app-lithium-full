package lithium.service.user.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
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
@Table(
    catalog = "lithium_user",
    name = "user_link_type"
)
/**
 * List of possible relationships available for users within an ecosystem if applicable or just the singular domain.<br/>
 * Available relationships are persisted during module loading.
 */
public class UserLinkType implements EntityWithUniqueCode, Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private Boolean linkDirectionSensitive; // Indicates whether a link type cares about who is the primary and secondary user

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
