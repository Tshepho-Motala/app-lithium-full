package lithium.service.casino.provider.rival.data.request;

import java.util.Map;

import lombok.ToString;

@ToString(callSuper=true)
public class BalanceRequest extends Request {
	public BalanceRequest(Map<String, String> allParams) {
		super(allParams);
	}
}