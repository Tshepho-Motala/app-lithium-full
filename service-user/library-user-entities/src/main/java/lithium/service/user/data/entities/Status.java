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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "name"})
@Table(catalog = "lithium_user",
    name = "status"
)
public class Status implements Serializable, EntityWithUniqueName {

  private static final long serialVersionUID = -2082517463977013077L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = false, unique = true)
  @Size(min = 2, max = 35)
  @Pattern(regexp = "^[a-zA-Z0-9_]+$")
  private String name;

  @Column(nullable = true)
  private String description;

  @Column(nullable = false)
  private Boolean userEnabled;

  @Column(nullable = false)
  private Boolean deleted;

  @PrePersist
  private void prePersist() {
    name = name.toUpperCase();
    if (deleted == null) {
      deleted = false;
    }
  }
}
