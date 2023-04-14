package lithium.service.user.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "user_job"
)
public class UserJob {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private int status;

  private int phoneLength;

  private int pageSize;

  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime completedDate;

  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime createdDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  private Domain domain;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @PrePersist
  void prePersist() {
    if(createdDate == null) {
      createdDate = DateTime.now();
    }
  }

}
