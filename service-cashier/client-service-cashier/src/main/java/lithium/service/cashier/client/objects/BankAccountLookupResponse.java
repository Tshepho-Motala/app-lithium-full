package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountLookupResponse implements Serializable {
	private String status;
	private String accountName;
	private String accountNumber;
	private String bankCode;
	private String bankName;
	private String failedStatusReasonMessage;
	private String code;
	private String message;
	private String userId;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String bankId;
}