package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"role", "name", "category"})
@Table(
    catalog = "lithium_user",
    name = "role",
    indexes = {
        @Index(name = "idx_role_role", columnList = "role", unique = true)
    })
@JsonIgnoreProperties({"grds", "hibernateLazyInitializer", "handler"})
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Role implements GrantedAuthority {

  private static final long serialVersionUID = 657316034667772170L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String role;
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  @JsonManagedReference("role_category")
  private Category category;

  @Transient
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
  @JsonBackReference("grd_role")
  private List<GRD> grds;

  public Role(String name, String role, String description) {
    super();
    this.name = name;
    this.role = role;
    this.description = description;
  }

  @Transient
  public String getNameCode() {
    return ("GLOBAL.ROLE." + role + ".NAME").toUpperCase();
  }

  @Transient
  public String getDescriptionCode() {
    return ("GLOBAL.ROLE." + role + ".DESCR").toUpperCase();
  }

  @Override
  public String getAuthority() {
    return getRole();
  }
}
