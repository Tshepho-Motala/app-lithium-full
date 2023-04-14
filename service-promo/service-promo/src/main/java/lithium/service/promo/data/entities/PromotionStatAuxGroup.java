package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;

import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
//@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PromotionStatAuxGroup implements Serializable {

	@Serial
	private static final long serialVersionUID = 1193895945324372311L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("promotionStat")
	private PromotionStat promotionStat;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(nullable=false)
//	private LabelValue labelValue;

	@OneToMany(fetch=FetchType.EAGER)
//	@JsonManagedReference("promotionStat")
	private List<LabelValue> labelValues;
}