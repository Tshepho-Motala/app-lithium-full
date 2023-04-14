package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PromotionStatEntry implements Serializable {
	@Serial
	private static final long serialVersionUID = -8564633573667968529L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private PromotionStat promotionStat;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(nullable=false)
//	private User owner;

	@Column(nullable=true)
	private Long value;

	@Builder.Default
	private DateTime entryDate = DateTime.now();

	@PrePersist
	void defaults() {
		if (entryDate == null) entryDate = DateTime.now();
	}
}
