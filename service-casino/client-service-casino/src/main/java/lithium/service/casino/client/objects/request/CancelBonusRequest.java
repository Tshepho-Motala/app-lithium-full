package lithium.service.casino.client.objects.request;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelBonusRequest extends Request implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer bonusId;
	private String gameId;
	private String userId;
}