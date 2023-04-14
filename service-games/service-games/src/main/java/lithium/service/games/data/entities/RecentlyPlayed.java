package lithium.service.games.data.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "recently_played",
    indexes = {
	    @Index(name = "idx_all", columnList = "game_id, user_id, lastUsed", unique = true), // FIXME: needed in foreign key constraint, this can be removed when recently played games is reworked
	    @Index(name = "idx_user_last_used", columnList = "user_id, lastUsed"),
		@Index(name = "idx_user_game", columnList = "user_id, game_id", unique = true)
	}
)
public class RecentlyPlayed {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Game game;

	@ManyToOne
	@JoinColumn(nullable = false)
	private User user;

	@Column(nullable = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date lastUsed;
}
