package lithium.service.libraryvbmigration.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class MigrationCredential implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String salt;
  private String hashedPassword;
  private short hashingAlgorithm;
  private String username;
  private String securityQuestion;
  private String securityQuestionAnswer;
  private String playerGuid;
  private String customerId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o != null) {
      Hibernate.getClass(this);
      Hibernate.getClass(o);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
