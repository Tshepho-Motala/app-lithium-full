package lithium.service.casino.provider.supera.data.response;

import lithium.service.casino.provider.supera.data.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class FreeroundRemoveResponse extends BaseResponse {
	private String response;
}