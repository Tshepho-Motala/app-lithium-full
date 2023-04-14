package lithium.service.casino.provider.supera.data.response;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.casino.provider.supera.data.BaseResponse;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class AvailableGamesResponse extends BaseResponse {
	@JsonProperty
	private List<Game> response;
}