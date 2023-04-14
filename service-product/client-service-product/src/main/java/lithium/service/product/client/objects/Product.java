package lithium.service.product.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"localCurrencies", "payouts"})
public class Product implements Serializable {
	private static final long serialVersionUID = -821688177929578897L;
	
	private String guid;
	private String name;
	private String description;
	@Default
	private Boolean enabled = false;
	private Domain domain;
	private String currencyCode;
	private BigDecimal currencyAmount;
	@Singular
	@JsonIgnoreProperties("product")
	private List<LocalCurrency> localCurrencies;
	
	@Singular
	@JsonIgnoreProperties("product")
	private List<Payout> payouts;
	
	private String notification;
}