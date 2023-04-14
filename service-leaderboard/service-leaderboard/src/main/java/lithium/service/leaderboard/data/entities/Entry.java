package lithium.service.leaderboard.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode(of={"id", "version"})
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_name", columnList="leaderboard_history_id, user_id", unique=true),
})
@NamedNativeQuery(
	name = "rankEntryMapping",
	resultClass = lithium.service.leaderboard.data.entities.Entry.class,
	query = "SELECT id, DENSE_RANK() OVER (PARTITION BY leaderboard_history_id ORDER BY points DESC, score DESC) as rank, score, points, user_id, leaderboard_history_id, version FROM entry WHERE leaderboard_history_id = :lhid"
)
//TODO: This has been taken out of code, db can do it much faster/efficiently, but mariadb does not support update from CTE yet, 
// so results are read, and then sent back to db to save/persist.. Needs to be revisited at a later stage, this is better solution for now.
public class Entry implements Serializable {
	private static final long serialVersionUID = 1026731794395084030L;
	
	public Entry(Long id, Integer rank, BigDecimal score, BigDecimal points, User user) {
		super();
		this.id = id;
		this.rank = rank;
		this.score = score;
		this.points = points;
		this.user = user;
	}
	
	public Entry(Long id, Integer rank, BigDecimal score, BigDecimal points, User user, LeaderboardHistory leaderboardHistory) {
		super();
		this.id = id;
		this.rank = rank;
		this.score = score;
		this.points = points;
		this.user = user;
		this.leaderboardHistory = leaderboardHistory;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JsonIgnoreProperties("entries")
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "leaderboard_history_id", nullable=false)
	private LeaderboardHistory leaderboardHistory;
	
	private Integer rank;
	@Default
	private BigDecimal score = BigDecimal.ZERO;
	@Default
	private BigDecimal points = BigDecimal.ZERO;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User user;
	
	public void addScore(BigDecimal add) {
		score = score.add(add);
	}
}