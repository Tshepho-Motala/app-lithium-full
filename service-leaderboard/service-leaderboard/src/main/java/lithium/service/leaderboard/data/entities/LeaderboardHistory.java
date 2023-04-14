package lithium.service.leaderboard.data.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_name", columnList="leaderboard_id, startDate, endDate", unique=true),
})
@ToString(exclude={"entries"})
public class LeaderboardHistory implements Serializable {
	private static final long serialVersionUID = -2281931685915546694L;
	
	public LeaderboardHistory(Long id, Leaderboard leaderboard) {
		super();
		this.id = id;
		this.leaderboard = leaderboard;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(nullable=false)
	private DateTime startDate;
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(nullable=false)
	private DateTime endDate;
	
	@Default
	private Boolean closed = Boolean.FALSE;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties("leaderboardHistory")
	@OneToMany(fetch=FetchType.EAGER, mappedBy="leaderboardHistory", cascade=CascadeType.MERGE)
	private List<Entry> entries;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnoreProperties("leaderboardHistories")
	private Leaderboard leaderboard;
}