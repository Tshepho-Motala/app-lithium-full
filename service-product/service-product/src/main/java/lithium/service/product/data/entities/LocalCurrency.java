package lithium.service.product.data.entities;

import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(indexes = {
	@Index(name="idx_curr_product", columnList="countryCode,currencyCode,product_id", unique=true)
})
public class LocalCurrency {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	private String countryCode;
	private String currencyCode;
	private BigDecimal currencyAmount;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonIgnoreProperties(value = {"localCurrencies"}, allowSetters = true)
	@JoinColumn(nullable=false)
	private Product product;
}
