package lithium.service.casino.client.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BonusAllocatev2 implements Serializable {
	private String bonusCode;
	private String playerGuid;
	private Double customAmountDecimal;
	private Integer customAmountNotMoney;
	private Long bonusRevisionId; // Use this if present otherwise get it from bonus code lookup and use current revision.
	private Long requestId;
	private String description;
	private String clientId;
	private Long sessionId;
	private String noteText;
	private SourceSystem sourceSystem;
}
