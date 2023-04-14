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

@Entity(name="domain")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain implements Serializable, EntityWithUniqueName {

  @Serial
  private static final long serialVersionUID = 5443350366948912738L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Version
  private int version;

  private String  name;

}
