package lithium.service.promo.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@ToString(exclude={"promotionRevision", "challenge"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude={"promotionRevision", "challenge"})
@Table
public class Reward implements Serializable {


	@Serial
	private static final long serialVersionUID = -5624062543759769733L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@JsonBackReference("PromotionReward")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private PromotionRevision promotionRevision;

	@JsonBackReference("ChallengeReward")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Challenge challenge;

	private Long rewardId;
}
