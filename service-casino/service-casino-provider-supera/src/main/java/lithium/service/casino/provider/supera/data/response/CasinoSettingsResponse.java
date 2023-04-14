package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.casino.provider.supera.data.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CasinoSettingsResponse extends BaseResponse {
	@JsonProperty
	private CasinoSettings response;
}