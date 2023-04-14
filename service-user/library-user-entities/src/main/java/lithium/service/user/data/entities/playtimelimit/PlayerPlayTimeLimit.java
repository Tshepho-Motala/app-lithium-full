package lithium.service.user.data.entities.playtimelimit;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import lithium.service.client.objects.Granularity;
import lithium.service.user.converter.EnumConverter.GranularityConverter;
import lithium.service.user.converter.EnumConverter.LimitTypeConverter;
import lithium.service.user.converter.LocalDateTimeAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
    catalog = "lithium_user",
    name = "player_play_time_limit",
    indexes = {
        @Index(name = "idx_pl_player_gran_type", columnList = "user_id, granularity, type"),
        @Index(name = "idx_domain_name_granularity_type_time_in_minutes_used", columnList = "domain_name, granularity, type, timeInMinutesUsed",
            unique = false)
    }
)
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class PlayerPlayTimeLimit implements Serializable {

  private static final long serialVersionUID = -1;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;
  @Column(name = "user_id", nullable = false)
  private long userId;
  @Column(name = "domain_name", nullable = false)
  private String domainName;
  @Column(nullable = false)
  @Convert(converter = GranularityConverter.class)
  private Granularity granularity;
  @Column(nullable = false)
  @Convert(converter = LimitTypeConverter.class)
  private LimitType type;
  @Column(nullable = false)
  private long timeInMinutes;
  @Column(nullable = false)
  private long timeInMinutesUsed;
  @Column(updatable = false)
  @CreatedDate
  private long createdDate;
  @LastModifiedDate
  private long modifiedDate;
  @Column(nullable = false)
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime lastReset;
  @Transient
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private DateTime appliedAt;
  @Transient
  private long timeLimitRemainingSeconds;

  @PrePersist
  public void prePersist() {
    if (lastReset == null) lastReset = LocalDateTime.now();
  }
}
