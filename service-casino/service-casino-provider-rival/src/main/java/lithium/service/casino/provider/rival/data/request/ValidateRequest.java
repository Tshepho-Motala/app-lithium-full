package lithium.service.casino.provider.rival.data.request;

import java.util.Map;

import lithium.service.casino.provider.rival.util.HashCalculator;
import lombok.ToString;

@ToString(callSuper=true)
public class ValidateRequest extends Request {
	public ValidateRequest(Map<String, String> allParams) {
		super(allParams);
	}

}
