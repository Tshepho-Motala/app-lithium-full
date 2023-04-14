package lithium.service.user.provider.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Type implements Serializable, EntityWithUniqueName {

  @Serial
  private static final long serialVersionUID = 3823263275070661197L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Version
  private int version;
  private String name;
}
