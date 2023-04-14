package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"domain", "role"})
@EqualsAndHashCode(exclude = {"id", "role", "group"})
@JsonIgnoreProperties({"group", "hibernateLazyInitializer", "handler"})
@Table(
    catalog = "lithium_user",
    name = "grd"
)
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class GRD implements Serializable {

  private static final long serialVersionUID = -8230785310847782674L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Boolean selfApplied; // Is this role applicable to the current domain ?
  private Boolean descending; // Is this role applicable to the children for this domain ?
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "group_id")
  @JsonBackReference("group_grds")
  private Group group;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  @JsonManagedReference("grd_role")
  private Role role;
  //	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "category_id")
//	@JsonManagedReference("grd_category")
//	private Category category;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  @JsonManagedReference("grd_domain")
  private Domain domain;
}
