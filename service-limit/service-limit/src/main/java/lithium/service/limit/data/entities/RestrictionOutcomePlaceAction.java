package lithium.service.limit.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Builder
@ToString(exclude="set")
@EqualsAndHashCode(exclude="set")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes={
		@Index(name="idx_code", columnList="set_id, code", unique=true)
})
public class RestrictionOutcomePlaceAction {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("set")
	private DomainRestrictionSet set;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TransactionProcessingCode code;
}
