package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import lithium.service.casino.provider.supera.data.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class FreeroundShowResponse extends BaseResponse {
	private List<FreeroundBetDetails> response = new ArrayList<FreeroundBetDetails>();
}