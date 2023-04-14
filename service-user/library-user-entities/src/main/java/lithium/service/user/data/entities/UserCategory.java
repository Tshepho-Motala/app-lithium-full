package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Entity(name = "user.UserCategory")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(of = {"name", "description", "domain", "dwhVisible", "users"})
@Table(catalog = "lithium_user",
    name = "user_category",
    indexes = {
        @Index(name = "idx_category_name", columnList = "name, domain_id", unique = true)
    })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserCategory implements Serializable {

  private static final long serialVersionUID = 5759954224544866945L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String description;
  private Boolean dwhVisible;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  @JsonManagedReference("user_domain")
  private Domain domain;

  @Singular
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userCategories", cascade = CascadeType.MERGE)
  @JsonIgnoreProperties("userCategories")
  private List<User> users;
}
