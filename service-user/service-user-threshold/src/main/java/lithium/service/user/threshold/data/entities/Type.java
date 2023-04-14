package lithium.service.user.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cache( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Table( indexes = {@Index( name = "idx_type_name", columnList = "name", unique = true )} )
public class Type implements Serializable, EntityWithUniqueName {

  @Serial
  private static final long serialVersionUID = 3823263275070661197L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;
  @Version
  private int version;
  private String name;
}