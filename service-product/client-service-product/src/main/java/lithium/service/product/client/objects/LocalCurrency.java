package lithium.service.product.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LocalCurrency implements Serializable {
	private static final long serialVersionUID = -18609440195049005L;
	private String countryCode;
	private String currencyCode;
	private BigDecimal currencyAmount;
	@JsonIgnoreProperties("localCurrencies")
	private Product product;
}
