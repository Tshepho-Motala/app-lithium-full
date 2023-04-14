package lithium.service.casino.client.objects.response;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateBonusIdResponse extends Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer bonusId;
	private Long externalBonusId; // So lithium player bonus id
}