package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userPromotionChallenge"})
@EqualsAndHashCode(exclude = {"userPromotionChallenge"})
public class UserPromotionChallengeRule implements Serializable {
	@Serial
	private static final long serialVersionUID = 6494123337895532127L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("userPromotionChallenge")
	private UserPromotionChallenge userPromotionChallenge;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Rule rule;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private PromotionStat promotionStat;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime started;
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime completed;

	private BigDecimal percentage;

	@Builder.Default
	private Boolean ruleComplete = Boolean.FALSE;

	public String getStartedDisplay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ((started != null)? started.format(formatter): "");
	}

	public String getCompletedDisplay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ((completed != null)? completed.format(formatter): "");
	}

	@PrePersist
	void defaults() {
		if (ruleComplete == null) ruleComplete = Boolean.FALSE;
	}
}