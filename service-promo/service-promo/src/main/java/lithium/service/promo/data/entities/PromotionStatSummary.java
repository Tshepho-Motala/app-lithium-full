package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;

import java.util.StringJoiner;
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

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PromotionStatSummary implements Serializable {
	@Serial
	private static final long serialVersionUID = -2747314429756233145L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private PromotionStat promotionStat;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User owner;

	@Builder.Default
	private Long value = 0L;

	@Override
	public String toString() {
		return new StringJoiner(", ", PromotionStatSummary.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("promotionStat=" + promotionStat.getName()+"("+promotionStat.getId()+")")
				.add("period=" + period.toShortString())
				.add("owner=" + owner.guid())
				.add("value=" + value)
				.toString();
	}
}
