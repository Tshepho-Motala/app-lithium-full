package lithium.service.casino.provider.supera.data.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.casino.provider.supera.data.BaseResponse;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class GetGameResponse extends BaseResponse {
	@JsonProperty("response")
	private GetGameDetails response;
}