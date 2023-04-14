package lithium.service.stats.data.entities;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_domain_type_event", columnList="domain_id, type_id, event_id", unique=true)
})
public class DomainStat implements Serializable {
	private static final long serialVersionUID = -634059477920636484L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Type type;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Event event;
}
