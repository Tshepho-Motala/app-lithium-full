package lithium.service.migration.models.enities;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
//import lithium.service.migration.util.MigrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.ObjectUtils;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private long totalNumberOfRows;
  private int chunkSize;
  private long lastRowProcessed;
  private boolean running;
  @Enumerated(EnumType.STRING)
  private MigrationType migrationType;
  private String customerId;
  @CreationTimestamp
  private LocalDateTime createdDate;
  @UpdateTimestamp
  private LocalDateTime updatedTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Progress progress = (Progress) o;
    return !ObjectUtils.isEmpty(id) && Objects.equals(id, progress.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
