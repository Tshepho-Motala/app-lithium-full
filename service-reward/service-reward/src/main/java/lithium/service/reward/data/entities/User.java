package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "user", indexes = {@Index( name = "idx_user_guid", columnList = "guid", unique = true )} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class User implements Serializable, EntityWithUniqueGuid {

  private static final long serialVersionUID = -2853587850932703107L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @Version
  private int version;

  @Column( nullable = false )
  private String guid;

  private String apiToken;
  private String originalId; //from svc-user

  @Column( name = "test_account", nullable = false )
  private boolean isTestAccount;

  /// Utility methods
  public String domainName() {
    return guid.split("/")[0];
  }

  public String username() {
    return guid.split("/")[1];
  }

  public String guid() {
    return guid;
  }
}
