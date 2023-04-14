package lithium.service.cashier.processor.netaxept.data;

import lithium.util.FormParam;
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
public class RegisterRequest {
	private String merchantId;
	private String token;
	private String transactionId;  //Can be provided by netaxept if not sent,  but I'm using order number (cashier transaction id) from our side here as well
	private String orderNumber;
	private String currencyCode;
	private String lanuage; // no_NO or en_US etc, default is no_NO
	private String redirectUrl;
	
	final private String autoSale = "true";
	final private String serviceType = "B";
	
	@FormParam("amount")
	private String amount;
	
}

 
