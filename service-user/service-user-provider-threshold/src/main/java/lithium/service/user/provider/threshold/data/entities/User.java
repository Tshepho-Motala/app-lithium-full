package lithium.service.user.provider.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueGuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable, EntityWithUniqueGuid {

  @Serial
  private static final long serialVersionUID = 447761365927264829L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Version
  private int version;
  private String guid;
  private boolean testAccount;
  private int dobYear;
  private int dobMonth;
  private int dobDay;
  private boolean notifications;
  @OneToOne
  private Domain domain;
  private String name;
}
