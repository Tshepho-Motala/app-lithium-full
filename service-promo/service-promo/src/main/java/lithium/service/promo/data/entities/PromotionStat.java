package lithium.service.promo.data.entities;

import java.io.Serializable;
import java.util.List;

import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import javax.persistence.OneToMany;

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
		@Index(name="idx_name", columnList="name, owner_id", unique=true)
})
public class PromotionStat implements Serializable {
	private static final long serialVersionUID = -7872393161114841016L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Column(nullable=false)
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User owner;

	@Column(nullable=false)
//	@Enumerated(EnumType.STRING)
	private String category; // casino, xp, user

	@Column(nullable=false)
//	@Enumerated(EnumType.STRING)
	private String activity; // casino: [spin, win, bonusround, seeit], xp: [level, points], user: [login], raf: [referral, conversion], avatar: [update]

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "promotionStat")
	@JsonManagedReference
	private List<PromotionStatSummary> promotionStatSummary;

//	@OneToMany(fetch=FetchType.EAGER, mappedBy="promotionStat", cascade=CascadeType.ALL)
//	@JsonManagedReference("promotionStat")
//	private List<MissionStatLabelValue> missionStatLabelValues;

	@Override
	public String toString() {
		return new StringJoiner(", ", PromotionStat.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("name='" + name + "'")
				.add("owner=" + owner.guid())
				.add("category='" + category + "'")
				.add("activity='" + activity + "'")
				.add("promotionStatSummary=" + promotionStatSummary)
				.toString();
	}
}