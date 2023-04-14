package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"userPromotion", "userPromotionChallengeGroup"})
public class UserPromotionChallenge implements Serializable {
	@Serial
	private static final long serialVersionUID = 1227530734917962161L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("userPromotion")
	private UserPromotion userPromotion;

	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.MERGE)
	@JoinColumn(nullable=false)
	private Challenge challenge;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("userPromotionChallengeGroup")
	private UserPromotionChallengeGroup userPromotionChallengeGroup;


	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime started;
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime completed;

	private BigDecimal percentage;

	//	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="userPromotionChallenge", cascade=CascadeType.MERGE)
	@JsonManagedReference("userPromotionChallenge")
	private List<UserPromotionChallengeRule> rules;

	@Builder.Default
	private Boolean challengeComplete = Boolean.FALSE;

//	public String getStartedDisplay() {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		return ((started != null)? started.format(formatter): "");
//	}
//
//	public String getCompletedDisplay() {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		return ((completed != null)? completed.format(formatter): "");
//	}

	public void addRule(UserPromotionChallengeRule rule) {
		if (rules == null) rules = new ArrayList<>();
		rules.add(rule);
	}
	@PrePersist
	void defaults() {
		if (challengeComplete == null) challengeComplete = Boolean.FALSE;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", UserPromotionChallenge.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("userPromotion=" + userPromotion.toShortString())
				.add("challenge=" + challenge)
				.add("userPromotionChallengeGroup=" + userPromotionChallengeGroup.toShortString())
				.add("started=" + started)
				.add("completed=" + completed)
				.add("percentage=" + percentage)
				.add("rules=" + rules)
				.add("challengeComplete=" + challengeComplete)
				.toString();
	}

}