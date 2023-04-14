package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
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
@ToString(exclude = {"domainMethodProfiles", "users"})
@JsonIgnoreProperties({"domainMethodProfiles", "users"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "profile"
)
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  private String code;
  private String name;
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Domain domain;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "profile_requirements_id")
  private ProfileRequirements profileRequirements;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "profile", cascade = CascadeType.ALL)
  private List<User> users = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL)
  private List<DomainMethodProfile> domainMethodProfiles = new ArrayList<>();

  @Column(nullable = false)
  private Boolean deleted;

  @PrePersist
  public void prePersist() {
    if (this.deleted == null) {
      this.deleted = false;
    }
  }
}
