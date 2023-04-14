package lithium.service.leaderboard.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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

import lithium.service.leaderboard.client.objects.Granularity;
import lithium.service.leaderboard.data.converter.EnumConverter.GranularityConverter;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@Table(indexes = {
	@Index(
		name="idx_leaderboard", 
		columnList="domain_id, xpLevelMin, xpLevelMax, xpPointsMin, xpPointsMax, xpPointsPeriod, xpPointsGranularity, startDate, recurrencePattern",
		unique=true
	),
})
@ToString(exclude={"leaderboardHistories", "leaderboardConversions", "leaderboardPlaceNotifications"})
public class Leaderboard implements Serializable {
	private static final long serialVersionUID = 1531797298766111934L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String name;
	@Column(nullable=true)
	private String description;
	@Default
	private Boolean visible = Boolean.FALSE;
	@Default
	private Boolean enabled = Boolean.FALSE;
	
	private Integer amount; // The amount of entries to keep for this leaderboard. e.g. top 50 / top 10
	
	private Integer xpLevelMin;
	private Integer xpLevelMax;
	
	private BigDecimal xpPointsMin;
	private BigDecimal xpPointsMax;
	private Integer xpPointsPeriod;
	@Column(nullable=false)
	@Convert(converter=GranularityConverter.class)
	private Granularity xpPointsGranularity;
	
	private BigDecimal scoreToPoints;
	
	private String notification;
	private String notificationNonTop;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(nullable=false)
	private DateTime startDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	private Integer durationPeriod;
	@Column(nullable=false)
	@Convert(converter=GranularityConverter.class)
	private Granularity durationGranularity;
	
	private String recurrencePattern;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties("leaderboard")
	@OneToMany(fetch=FetchType.EAGER, mappedBy="leaderboard", cascade=CascadeType.MERGE)
	private List<LeaderboardConversion> leaderboardConversions;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties("leaderboard")
	@OneToMany(fetch=FetchType.EAGER, mappedBy="leaderboard", cascade=CascadeType.MERGE)
	private List<LeaderboardPlaceNotification> leaderboardPlaceNotifications;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties("leaderboard")
	@OneToMany(fetch=FetchType.EAGER, mappedBy="leaderboard", cascade=CascadeType.MERGE)
	private List<LeaderboardHistory> leaderboardHistories;
}