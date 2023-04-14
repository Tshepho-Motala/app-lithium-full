package lithium.service.games.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
@Table(
  name = "game_graphic",
  indexes = {
		@Index(name="idx_gg_gameid", columnList="gameId", unique=false),
		@Index(name="idx_gg_gameid_function", columnList="gameId, graphic_function_id", unique=false),
		@Index(name = "idx_gg_deleted_enabled_gameid_function_livecasino", columnList = "deleted, enabled, gameId, graphic_function_id, liveCasino", unique = true)
	}
)
public class GameGraphic implements Serializable {

	private static final long serialVersionUID = 1L;
	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private boolean enabled;
	
	@Column(nullable=false)
	private long gameId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Graphic graphic;

	@Column
  @URL(message="Invalid URL")
  private String url;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private GraphicFunction graphicFunction;
	
	private boolean deleted;

	private boolean liveCasino;
}
