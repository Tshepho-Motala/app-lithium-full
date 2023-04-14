package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.promo.client.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="challenge")
@Table
@Where(clause = "deleted=false")
public class Rule implements Serializable {
	@Serial
	private static final long serialVersionUID = 7449746196633812802L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@JsonBackReference("Rule")
	@ManyToOne(fetch=FetchType.EAGER)
	private Challenge challenge;
	
	/*
	 *  dm.<missionId>.casino.spin.<gameId> = 15/50
		dm.<missionId>.casino.win = 5
		dm.<missionId>.casino.bonusround = 3
		dm.<missionId>.xp.level = 1
		dm.<missionId>.xp.points = 300
		dm.<missionId>.user.login = 2
		dm.<missionId>.user.raf = 3
	 */

	/**
	 * At implementation this was moved to PromoProvider, but we now need this back on this level:
	 * PLAT-11927 - PROM - BE - Allow cross provider challenge
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category; // casino, xp, user, raf
	@ManyToOne(fetch=FetchType.EAGER)
	private PromoProvider promoProvider;

	@ManyToOne(fetch=FetchType.EAGER)
	private Activity activity;

	@Column(nullable = false)
	@Enumerated( EnumType.STRING)
	private Operation operation;

	@Column(nullable=false)
	private Long value;

	@Builder.Default
	private Boolean deleted = Boolean.FALSE;

	@Override
	public String toString() {
		return new StringJoiner(", ", Rule.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("challenge=" + challenge.toShortString())
				.add("promoProvider=" + promoProvider)
				.add("category=" + Optional.ofNullable(category).map(Category::getName))
				.add("activity=" + activity.getName())
				.add("operation=" + operation.type())
				.add("value=" + value)
				.toString();
	}
}
