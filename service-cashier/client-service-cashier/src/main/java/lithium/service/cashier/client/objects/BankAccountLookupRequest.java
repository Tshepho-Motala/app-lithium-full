package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountLookupRequest implements Serializable {
	private Map<String, String> domainMethodProcessorProperties;
	private String accountNumber;
	private String bankCode;
	private String bankName;
}