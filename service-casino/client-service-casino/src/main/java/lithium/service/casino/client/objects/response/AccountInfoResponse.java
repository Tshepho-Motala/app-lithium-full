package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponse extends Response {
	private Long userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String currency;
	private Long balanceCents;
}