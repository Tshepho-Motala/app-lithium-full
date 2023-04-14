package lithium.service.games.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    indexes = {
        @Index(name = "idx_game_channel", columnList = "game_id, channel_id", unique = true)
    }
)
public class GameChannel implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch= FetchType.EAGER)
  @JoinColumn(nullable=false)
  @JsonBackReference
  private Game game;

  @ManyToOne(fetch= FetchType.EAGER)
  @JoinColumn(nullable=false)
  private Channel channel;

  @Version
  private int version;
}
