package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "description"})
@Table(
    catalog = "lithium_user",
    name = "category",
    indexes = {
        @Index(name = "idx_category_name", columnList = "name", unique = true)
    })
@JsonIgnoreProperties({"roles"})
@EqualsAndHashCode(exclude = {"id", "roles"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Category implements Serializable {

  private static final long serialVersionUID = -4674034648629362157L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String description;

  //	@Transient
  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
  @JsonBackReference("role_category")
  private List<Role> roles;

  @Transient
  public String getNameCode() { return ("GLOBAL.CAT." + id + ".NAME").toUpperCase(); }

  @Transient
  public String getDescriptionCode() { return ("GLOBAL.CAT." + id + ".DESCR").toUpperCase();  }
}
