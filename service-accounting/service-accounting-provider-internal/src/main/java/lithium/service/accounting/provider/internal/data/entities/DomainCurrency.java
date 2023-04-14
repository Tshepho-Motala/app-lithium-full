package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@Table(
		name = "domain_currency",
		indexes = {
				@Index(name = "idx_domain_currency", columnList = "domain_id, currency_id", unique = true),
		}
)
public class DomainCurrency {
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
	private Currency currency;
	
	@Column(nullable=false)
	private Boolean isDefault;
	
	@Column(name="`name`", nullable=false)
	private String name;
	
	@Column(name="`symbol`", nullable=false)
	private String symbol;
	
	@Column(nullable=false)
	private Integer divisor;
	
	@Column(nullable=true)
	private String description;
	
	@PrePersist
	public void prePersist() {
		if (isDefault == null) isDefault = false;
		if (name == null) name = currency.getName();
		if (symbol == null) symbol = currency.getCode();
		if (divisor == null) divisor = 100;
	}
}
