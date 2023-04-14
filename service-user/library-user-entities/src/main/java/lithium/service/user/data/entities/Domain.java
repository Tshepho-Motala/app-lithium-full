package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Entity(name = "user.Domain")
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "domain",
    indexes = {
        @Index(name = "idx_domain_name", columnList = "name", unique = true)
    }
)
@JsonIgnoreProperties({"grds", "users", "groups"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Domain implements Serializable {

  private static final long serialVersionUID = -6568665804594566561L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private Boolean isTestDomain;

  @Transient
  @OneToMany(fetch = FetchType.LAZY)
  @JsonBackReference("grd_domain")
  private List<GRD> grds;

  @Transient
  @OneToMany(fetch = FetchType.LAZY)
  @JsonBackReference("user_domain")
  private List<User> users;

  @Transient
  @OneToMany(fetch = FetchType.LAZY)
  @JsonBackReference("group_domain")
  private List<Group> groups;

  public boolean isComplete() {
    return (id != null) && ((name != null) && (!name.isEmpty()));
  }
}
