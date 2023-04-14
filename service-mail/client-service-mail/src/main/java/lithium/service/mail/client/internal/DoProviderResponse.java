package lithium.service.mail.client.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DoProviderResponse {
	private Long mailId;
	private String from;
	private String bcc;
	private DoProviderResponseStatus status;
	private String message;
}