package lithium.service.games.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class ChannelMigration {
  @Id
  private long id;

  @Version
  private int version;

  @Column(nullable = false)
  private boolean running = false;
}
