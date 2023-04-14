package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CasinoConfig {
	@JsonProperty
	public String render;
	@JsonProperty
	public Float denom;
}