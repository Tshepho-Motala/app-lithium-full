package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "description"})
@Table(
    catalog = "lithium_user",
    name = "group_table",
    indexes = {
        @Index(name = "idx_group_name_domain", columnList = "name, domain_id", unique = true)
    }
)
@JsonIgnoreProperties({"users", "hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Group implements Serializable {

  private static final long serialVersionUID = -8815025536829955387L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String description;
  private Boolean enabled = Boolean.TRUE;
  private Boolean deleted = Boolean.FALSE;

  @Transient
  @OneToMany(fetch = FetchType.EAGER)
  @JsonManagedReference("group_grds")
  private List<GRD> grds;

  @Transient
  @ManyToMany(mappedBy = "groups")
  @JsonBackReference("user_groups")
  private List<User> users;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  @JsonManagedReference("group_domain")
  private Domain domain;
}
