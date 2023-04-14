package lithium.service.games.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@ToString(exclude="game")
@EqualsAndHashCode(exclude="game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
  name = "game_currency",
  indexes = {
	  @Index(name="idx_game", columnList="game_id", unique=true)
  }
)
public class GameCurrency implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JsonBackReference("game")
	@ManyToOne
	private Game game;
	
	@Column(nullable=false)
	private String currencyCode;
	
	@Column(nullable=false)
	private Long minimumAmountCents;
}
