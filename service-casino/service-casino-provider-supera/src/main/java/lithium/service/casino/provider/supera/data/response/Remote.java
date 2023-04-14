package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Remote {
	@JsonProperty
	public String key;
}