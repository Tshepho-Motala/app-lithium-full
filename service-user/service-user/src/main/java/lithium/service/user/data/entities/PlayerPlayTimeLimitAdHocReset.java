package lithium.service.user.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class PlayerPlayTimeLimitAdHocReset {
  @Id
  private long id;

  @Version
  private int version;

  @Column(nullable = false)
  private boolean running = false;
}
