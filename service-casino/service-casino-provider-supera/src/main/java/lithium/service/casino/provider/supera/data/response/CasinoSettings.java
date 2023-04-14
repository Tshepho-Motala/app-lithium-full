package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CasinoSettings {
	@JsonProperty
	public Integer id;
	@JsonProperty
	public String name;
	@JsonProperty
	public CasinoConfig config;
	@JsonProperty
	public String accessKey;
	@JsonProperty
	public Remote remote;
}