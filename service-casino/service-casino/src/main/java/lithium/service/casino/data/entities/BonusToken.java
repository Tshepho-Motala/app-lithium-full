package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"bonusRevision"})
@EqualsAndHashCode(exclude={"bonusRevision"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class BonusToken {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;

	@Column(nullable=false)
	private String currency;
	
	@Column(nullable=false)
	private Long amount;
	
	@Column
	private Double minimumOdds;

	@JsonBackReference("bonusRevision")
	@ManyToOne(fetch= FetchType.LAZY)
	private BonusRevision bonusRevision;
}
