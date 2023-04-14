package lithium.service.casino.provider.rival.data.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper=true)
public class RollbackRequest extends Request {
	@JsonProperty(value="id")
	private String requestId;
	
	public RollbackRequest(Map<String,String> allParams) {
		super(allParams);
		requestId = allParams.get("id");
	}

}
