package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "incomplete_user"
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class IncompleteUser implements Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String email;
  private String firstName;
  private String lastNamePrefix;
  private String lastName;
  private String cellphoneNumber;
  private String countryCode;
  private Date createdDate;
  private Date lastUpdatedDate;
  private String stage;
  @ManyToOne
  private Domain domain;
  private String gender;
  private Integer dobYear;
  private Integer dobMonth;
  private Integer dobDay;
  private Long status;

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "residential_address_id")
  @JsonManagedReference("user_address")
  private Address residentialAddress;

  @JsonManagedReference
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "incompleteUser")
  @JsonIgnore
  private List<IncompleteUserLabelValue> incompleteUserLabelValueList;

  @PrePersist
  public void init() {
    if (createdDate == null) {
      createdDate = Calendar.getInstance().getTime();
    }
    lastUpdatedDate = Calendar.getInstance().getTime();
  }
}
