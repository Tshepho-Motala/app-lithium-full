package lithium.service.domain.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lithium.jpa.entity.EntityWithUniqueName;
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
 * Serves as a container entity for a group of domains that have certain relationships
 */
public class Ecosystem implements EntityWithUniqueName, Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = false, unique = true)
  @Size(min = 2, max = 35)
  @Pattern(regexp = "^[_a-z0-9\\.]+$")
  private String name;

  @Column(nullable = false)
  @Size(min = 2, max = 65, message = "No more than 30 and no less than 2 characters")
  private String displayName;

  @Column(nullable = true)
  private String description;

  @Column(nullable = false)
  private Boolean enabled;

  @Builder.Default
  @Column(nullable = false)
  private Boolean deleted = false;
}
