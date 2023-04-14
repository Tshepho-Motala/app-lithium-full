package lithium.service.casino.provider.supera.data;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
public class BaseResponse {
	@JsonProperty
	private int status;
}