package lithium.service.user.data.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;

@Builder
@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player_playtime_limit_v2_config_revision")
public class PlayerPlaytimeLimitV2ConfigRevision {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @ToString.Exclude
  private User user;

  @ManyToOne
  @JoinColumn
  private Granularity granularity;

  @ManyToOne
  @JoinColumn
  @ToString.Exclude
  private User createdBy;

  @Column(nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdDate;

  @Column(nullable = false)
  private LocalDateTime effectiveFrom;

  @Version
  private int version;

  private long secondsAllocated;

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
}
