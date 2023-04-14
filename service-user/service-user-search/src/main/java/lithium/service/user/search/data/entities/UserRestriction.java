package lithium.service.user.search.data.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_search.UserRestriction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user_search",
    name = "user_restrictions"
)
public class UserRestriction implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @OneToOne
  @JoinColumn(name = "set_id")
  private DomainRestriction domainRestriction;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, name = "active_from")
  private Date activeFrom;

  @Column(nullable = true, name = "active_to")
  private Date activeTo;
}
