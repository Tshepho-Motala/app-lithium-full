package lithium.service.leaderboard.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.casino.client.data.BonusRevision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_name", columnList="leaderboard_id, rank", unique=true),
})
public class LeaderboardPlaceNotification implements Serializable {
	private static final long serialVersionUID = -7721119777782062645L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnoreProperties("leaderboardPlaceNotifications")
	private Leaderboard leaderboard;
	
	private String bonusCode;
	private String notification;
	
	private Integer rank;

	@Transient
	private BonusRevision bonusRevision;
}
