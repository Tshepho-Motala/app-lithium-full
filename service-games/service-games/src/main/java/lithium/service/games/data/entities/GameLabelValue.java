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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="deleted")
@Table(
  name = "game_label_value",
  indexes = {
		@Index(name="idx_glv_gameid", columnList="gameId", unique=false),
		@Index(name="idx_glv_gameid_labelvalue", columnList="gameId, label_value_id, deleted", unique=true)
  }
)
public class GameLabelValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	private int version;

	@Column(nullable=false)
	private Long gameId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private LabelValue labelValue;
	
	@Column(nullable=false)
	private boolean deleted; //Will use parent label if deleted
	
	@Column(nullable=false)
	private boolean enabled; //Will effectively show/hide label from frontend display.
}
