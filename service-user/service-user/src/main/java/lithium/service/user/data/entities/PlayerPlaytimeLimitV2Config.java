package lithium.service.user.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Builder
@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player_playtime_limit_v2_config",
    indexes = {
        @Index(name = "idx_user", columnList = "user_id")
    }
)
public class PlayerPlaytimeLimitV2Config {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne
  @JoinColumn
  @ToString.Exclude
  private User user;

  @ManyToOne
  @JoinColumn
  private PlayerPlaytimeLimitV2ConfigRevision currentConfigRevision;

  @ManyToOne
  @JoinColumn
  private PlayerPlaytimeLimitV2ConfigRevision pendingConfigRevision;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o != null) {
      Hibernate.getClass(this);
      Hibernate.getClass(o);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public String domainName() {
    return user.domainName();
  }
}
