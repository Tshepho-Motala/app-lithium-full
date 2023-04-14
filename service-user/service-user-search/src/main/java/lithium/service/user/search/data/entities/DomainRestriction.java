package lithium.service.user.search.data.entities;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_search.DomainRestriction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user_search",
    name = "domain_restrictions"
)
public class DomainRestriction implements Serializable {
  @Id
  private long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, name = "domain_name")
  private String domainName;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false, name = "deleted")
  private boolean deleted;
}
