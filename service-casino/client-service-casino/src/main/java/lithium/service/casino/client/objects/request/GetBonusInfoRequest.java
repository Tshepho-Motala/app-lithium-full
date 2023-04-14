package lithium.service.casino.client.objects.request;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetBonusInfoRequest extends Request implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;

	private String bankId;
	
	private String gameId;

}