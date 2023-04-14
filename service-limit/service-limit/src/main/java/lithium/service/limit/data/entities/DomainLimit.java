package lithium.service.limit.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_dl_domain_gran_type", columnList="domainName, granularity, type", unique=true),
	@Index(name="idx_dl_domain", columnList="domainName")
})
public class DomainLimit implements Serializable {
	private static final long serialVersionUID = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@Column(nullable=false)
	private String domainName;

	@Column(nullable=false)
	private int granularity;
	
	@Column(nullable=false)
	private long amount; //Net loss/win amount
	
	@Column(nullable=false)
	private int type;
}