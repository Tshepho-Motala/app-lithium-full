package lithium.service.user.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueGuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( indexes = {@Index( name = "idx_user_guid", columnList = "guid", unique = true )} )
public class User implements Serializable, EntityWithUniqueGuid {

  @Serial
  private static final long serialVersionUID = -7812545992728985866L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;
  @Version
  private int version;
  private String guid;
  private boolean testAccount;
  private Integer dobYear;
  private Integer dobMonth;
  private Integer dobDay;
  private boolean notifications;
  @ManyToOne
  @JoinColumn( nullable = false )
  private Domain domain;
  private String username;

  private Date accountCreationDate;

  public String toShortString() {
    return new StringJoiner(", ", User.class.getSimpleName() + "[", "]").add("id=" + id).add("guid='" + guid + "'").toString();
  }
}
