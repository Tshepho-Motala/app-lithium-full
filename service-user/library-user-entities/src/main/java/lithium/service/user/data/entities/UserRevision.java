package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
@Table(
    catalog = "lithium_user",
    name = "user_revision",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id", unique = false)
    })
public class UserRevision implements Serializable {

  private static final long serialVersionUID = 1L;
  @Version
  int version;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @ManyToOne
  @JsonBackReference
  private User user;

  private Date creationDate;


  //	@Transient
  @JsonManagedReference
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "userRevision")
  private List<UserRevisionLabelValue> labelValueList;

  @PrePersist
  public void defaults() {
    if (creationDate == null) {
      creationDate = new Date();
    }
  }
}
