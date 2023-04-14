package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

@Data
@Table
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted=false")
public class Challenge implements Serializable {
	@Serial
	private static final long serialVersionUID = -4924462704248597163L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private Integer sequenceNumber;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "challenge_group_id")
	@JsonBackReference("challengeGroup")
	private ChallengeGroup challengeGroup;

	@Column(nullable=true)
	private String description;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Graphic icon;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonManagedReference("ChallengeReward")
	private Reward reward;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="challenge", cascade=CascadeType.MERGE)
	@JsonManagedReference("Rule")
	private List<Rule> rules;

	@Builder.Default
	private Boolean deleted = Boolean.FALSE;

	@Builder.Default
	private Boolean requiresAllRules = Boolean.FALSE;

	@Override
	public String toString() {
		return new StringJoiner(", ", Challenge.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("challengeGroup=" + challengeGroup.toShortString())
				.add("description='" + description + "'")
				.add("reward=" + reward)
				.add("rules=" + rules)
				.toString();
	}

	public String toShortString() {
		return new StringJoiner(", ", Challenge.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("challengeGroup=" + challengeGroup.toShortString())
				.add("description='" + description + "'")
				.toString();
	}
}
