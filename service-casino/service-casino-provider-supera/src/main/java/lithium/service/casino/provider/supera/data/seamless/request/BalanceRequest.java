package lithium.service.casino.provider.supera.data.seamless.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class BalanceRequest {
	private Integer remoteId;
	private String remoteData;
	private String sessionId;
	
	public BalanceRequest(Integer remoteId) {
		this.remoteId = remoteId;
	}
}