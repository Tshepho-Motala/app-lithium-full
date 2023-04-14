package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.StringJoiner;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
		@Index(name="idx_user_promotion", columnList="user_id, promotion_revision_id, period_id", unique=false)
})
public class UserPromotion implements Serializable {
	@Serial
	private static final long serialVersionUID = -3444277867391358882L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User user;

	private String timezone;

	@ManyToOne(fetch = FetchType.EAGER)
//	@JsonBackReference("promotion")
	@JoinColumn(nullable=false)
	private PromotionRevision promotionRevision;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime started;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime completed;
	@Builder.Default
	private Boolean expired = Boolean.FALSE;
	@Builder.Default
	private Boolean active = Boolean.TRUE;

	private BigDecimal percentage;


	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER, mappedBy="userPromotion")
	@JsonManagedReference("userPromotion")
	private List<UserPromotionChallengeGroup> userChallengeGroups;

	@Builder.Default
	private Boolean promotionComplete = Boolean.FALSE;

	public String getStartedDisplay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ((started != null)? started.format(formatter): "");
	}

	public String getCompletedDisplay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ((completed != null)? completed.format(formatter): "");
	}

	public boolean getIsOnCurrentPromotion() {
		return promotionRevision.getId().equals(promotionRevision.getPromotion().getCurrent().getId());
	}

	public void addChallengeGroup(UserPromotionChallengeGroup challengeGroup) {
		if (userChallengeGroups == null) userChallengeGroups = new ArrayList<>();
		userChallengeGroups.add(challengeGroup);
	}
	@PrePersist
	void defaults() {
		if (active == null) active = Boolean.TRUE;
		if (expired == null) expired = Boolean.FALSE;
		if (promotionComplete == null) promotionComplete = Boolean.FALSE;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", UserPromotion.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("user=" + user.guid())
				.add("timezone='" + timezone + "'")
				.add("promotionRevision=" + promotionRevision.getId())
				.add("period=" + period.toShortString())
				.add("started=" + started)
				.add("completed=" + completed)
				.add("expired=" + expired)
				.add("active=" + active)
				.add("percentage=" + percentage)
				.add("promotionComplete=" + promotionComplete)
				.add("userChallengeGroups=" + userChallengeGroups)
				.toString();
	}

	public String toShortString() {
		return new StringJoiner(", ", UserPromotion.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("user=" + user.guid())
				.add("promotionRevision=" + promotionRevision.getId())
				.toString();
	}
}