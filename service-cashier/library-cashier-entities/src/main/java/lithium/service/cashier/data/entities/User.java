package lithium.service.cashier.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity(name = "cashier.User")
@Builder
@ToString(exclude = {"userCategories"})
@EqualsAndHashCode(exclude = {"userCategories"})
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "user",
    indexes = {
        @Index(name = "idx_user_guid", columnList = "guid", unique = true)
    })
public class User implements Serializable {

  private static final long serialVersionUID = -2853587850932703107L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(nullable = false)
  private String guid; //is: domain/username

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "limits_id", nullable = true)
  private Limits limits;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_id", nullable = true)
  private Profile profile;

  @Column(name = "test_account", nullable = false)
  private boolean testAccount;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "status_id")
  private long statusId;

  @OneToMany(fetch = FetchType.LAZY, mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
  private List<UserCategory> userCategories = new ArrayList<>();

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

  public boolean hasProfile() {
    return (profile != null) && (profile.getId() != null);
  }

  /**
   * Return a reproducible semi-unique username that is no more than 20 chars
   *
   * @return
   */
  public String username20() {
    String username = username();
    if (username.length() <= 20) {
      return username;
    } else {
      String username20 = username.substring(0, 15);
      byte[] over15Chars = username.substring(15).getBytes();
      long stringValue = 0;
      for (int i : over15Chars) {
        stringValue += i;
      }
      username20 = username20 + stringValue;
      return username20;
    }
  }
}
