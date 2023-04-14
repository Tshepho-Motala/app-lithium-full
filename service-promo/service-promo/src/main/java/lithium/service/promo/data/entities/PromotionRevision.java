package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.StringJoiner;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="promotion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="promotion")
@Table(
		name = "promotion_revision",
		indexes = {
				@Index(name="idx_domain", columnList="domain_id", unique=false),
				@Index(name="idx_domain_start_end_date", columnList="domain_id, startDate, endDate, xpLevel", unique=false)
		})
public class PromotionRevision implements Serializable {
	@Serial
	private static final long serialVersionUID = -8660193343333428856L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@JsonBackReference("promotion")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Promotion promotion;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@Column(nullable=false)
	private String name;

	@Column(nullable=true)
	private String description;

	@Column(nullable=true)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime startDate;

	@Column(nullable=true)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime endDate;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="promotionRevision", cascade = CascadeType.PERSIST)
	@JsonManagedReference("UserCategory")
	private List<UserCategory>  userCategories;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="promotionRevision", cascade = CascadeType.PERSIST)
	@JsonManagedReference("challengeGroups")
	private List<ChallengeGroup> challengeGroups;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonManagedReference("PromotionReward")
	private Reward reward;

	@Column(nullable=true)
	private Integer xpLevel;

	@Builder.Default
	private Boolean exclusive = Boolean.FALSE;

	@Builder.Default
	private Boolean requiresAllChallengeGroups = Boolean.FALSE;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "promo_revision_exclusive_players",
			uniqueConstraints = {
					@UniqueConstraint(name = "idx_unique_revision_user", columnNames = { "promotion_revision_id", "player_id" })
			},
			joinColumns = {	@JoinColumn(name = "promotion_revision_id") },
			inverseJoinColumns = { @JoinColumn(name = "player_id") }
	)
	@Builder.Default
	private Set<User> exclusivePlayers = new HashSet<>();

	@ManyToOne
	@JoinColumn
	private Promotion dependsOnPromotion;

	private String recurrencePattern;
	private Integer redeemableInTotal; //This is for the lifetime of the promotion
	private Integer redeemableInEvent; //This is for each occurrence of the promotion
	private Integer eventDuration;
	private Integer eventDurationGranularity; //TODO: populate granularity table on startup, and link to relevant row.

	public String toShortString() {
		return new StringJoiner(", ", PromotionRevision.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("domain=" + domain)
				.add("name='" + name + "'")
				.toString();
	}

	public void addExclusivePlayer(User player) {
		exclusivePlayers.add(player);
	}
}
