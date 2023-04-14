package lithium.service.user.data.entities;

import lithium.service.user.converter.LocalDateTimeAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_active_sessions_metadata",
    indexes = {
      @Index(name = "idx_user", columnList = "user_id", unique = true)
    }
)
public class UserActiveSessionsMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne
  @JoinColumn
  private User user;

  @Column(nullable = false)
  private int activeSessionCount;

  @Column
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime playtimeLimitLastUpdated;

  @Transient
  private boolean createdNow = false;
}
