package lithium.service.user.provider.vipps.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
	private String errorCode;
	private String errorMessage;
}