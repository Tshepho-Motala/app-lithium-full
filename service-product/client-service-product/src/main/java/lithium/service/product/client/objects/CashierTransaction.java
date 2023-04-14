package lithium.service.product.client.objects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CashierTransaction {
	private Long transactionId;
	private Date createdOn;
	
	private Long amountCents;
	private String currencyCode;
	
	private String processorCode;
	private String methodCode;
	
	private Long domainMethodProcessorId;
	private String domainMethodName;
	private Long domainMethodId;
}