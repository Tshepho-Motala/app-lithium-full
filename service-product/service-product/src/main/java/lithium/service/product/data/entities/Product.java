package lithium.service.product.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"localCurrencies", "payouts"})
@Table(indexes = {
	@Index(name="idx_guid_domain", columnList="guid,domain_id", unique=true)
})
public class Product implements Serializable {
	private static final long serialVersionUID = -2155579846215778739L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String guid;
	@Column(nullable=false)
	private String name;
	private String description;
	@Default
	private Boolean enabled = false;
	
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JsonIgnoreProperties("products")
//	@JoinColumn(nullable=false)
//	private Catalog catalog;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	private String currencyCode;
	private BigDecimal currencyAmount;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties(value = {"product"}, allowSetters = true)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="product", cascade=CascadeType.MERGE)
	private List<LocalCurrency> localCurrencies;
	
	@Singular
	@Fetch(FetchMode.SELECT)
	@JsonIgnoreProperties(value = {"product"}, allowSetters = true)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="product", cascade=CascadeType.MERGE)
	private List<Payout> payouts;
	
	private String notification;
	
}
