package lithium.service.casino.provider.supera.data;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class BaseRequest {
	private String action;
	
	public abstract Map<String, String> parameters();
}