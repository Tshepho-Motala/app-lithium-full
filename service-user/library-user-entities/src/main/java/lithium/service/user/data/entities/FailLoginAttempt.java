package lithium.service.user.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
    catalog = "lithium_user",
    name = "fail_login_attempt",
    indexes = {
        @Index(name = "idx_domain", columnList = "domainName", unique = false),
        @Index(name = "idx_date_added", columnList = "dateAdded", unique = false)
    })
public class FailLoginAttempt implements Serializable {

  private static final long serialVersionUID = 1L;
  @Version
  int version;
  @Id
  private String ip;
  @Column(nullable = false)
  private String domainName;
  @Column(nullable = false)
  private Integer failureAmount;
  @Builder.Default
  @Column(nullable = true)
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime dateAdded = DateTime.now();
}
