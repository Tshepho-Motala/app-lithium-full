package lithium.service.casino.client.objects.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import lithium.service.casino.CasinoTranType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

//TODO @Chris to document the alternative if this is deprecated. This is used in GG, the latest casino provider.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
// Lets flag this as @Deprecated as soon as we have created test cases for the replacement.
public class BetRequest extends Request {
	private static final long serialVersionUID = -6394590860250159678L;
	private String userGuid;
	private Long bet;
	private Long win;
	private String roundId;
	private String gameGuid;
	private Boolean roundFinished;
	private String gameSessionId;
	private Long negativeBet;
	private String transactionId;
	private Boolean bonusTran = false;
	private Integer bonusId;
	private String currencyCode;
	private Boolean alwaysReal;
	private CasinoTranType tranType;
	private Long originalTransactionId;
	private Long bonusTokenId = null;
	private String additionalReference = null;

	private Boolean persistRound;
	private String betTransactionId; // The casino provider's bet reference for the round
	private Boolean checkSequence;
	private Integer sequenceNumber;
	private Long transactionTimestamp;
	private Double returns;
	private Long sessionId;
}
