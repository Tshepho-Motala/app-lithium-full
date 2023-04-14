package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "user_api_token",
    indexes = {
        @Index(name = "idx_uat_guid", columnList = "guid", unique = true),
        @Index(name = "idx_short_guid_user", columnList = "shortGuid, user_id", unique = true)
    }
)
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class UserApiToken implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false, unique = true)
  private String guid; // domainName/userName

  @Column(nullable = true, unique = true, length = 15)
  private String shortGuid; //firstname + random numbers

  @JsonBackReference("user")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;
}
