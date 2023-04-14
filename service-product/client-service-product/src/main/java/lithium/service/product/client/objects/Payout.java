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
public class Payout implements Serializable {
	private static final long serialVersionUID = -7152877848541737136L;

	private String bonusCode;
	
	private String currencyCode;
	private BigDecimal currencyAmount;
	
	@JsonIgnoreProperties("payouts")
	private Product product;
}