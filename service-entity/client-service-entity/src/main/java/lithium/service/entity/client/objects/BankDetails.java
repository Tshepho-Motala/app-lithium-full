package lithium.service.entity.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BankDetails {
	private Long id;
	private String bankName;
	private String branchCode;
	private String accountNumber;
	private String accountType;
	private String accountHolder;
}
