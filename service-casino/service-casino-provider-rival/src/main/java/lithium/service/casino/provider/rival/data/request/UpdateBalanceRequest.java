package lithium.service.casino.provider.rival.data.request;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper=true)
public class UpdateBalanceRequest extends Request {
	@JsonProperty(value="id")
	private String requestId;
	private String amount;
	@JsonProperty(value="transid")
	private String tranId;
	@JsonProperty(value="minbalance")
	private String minBalance; //Assert if player has this amount in balance before accepting tran
	@JsonProperty(value="gameid")
	private String gameId;
	@JsonProperty(value="rootgameid")
	private String rootGameId; //Only available when gameId differs from root game id (like in bonus games);
	@JsonProperty(value="parenttransid")
	private String parentTransId;
	
	public UpdateBalanceRequest(Map<String,String> allParams) {
		super(allParams);
		requestId = allParams.get("id");
		amount = allParams.get("amount");
		tranId = allParams.get("transid");
		minBalance = allParams.get("minbalance");
		gameId = allParams.get("gameid");
		rootGameId = allParams.get("rootgameid");
		parentTransId = allParams.get("parenttransid");
	}

	
	public Long getAmountCents() {
		BigDecimal bd = new BigDecimal(amount);
		bd = bd.movePointRight(2);
		return bd.longValue();
	}
	
	public Long getMinBalanceCents() {
		if (minBalance != null && !minBalance.isEmpty()) {
			BigDecimal bd = new BigDecimal(minBalance);
			bd = bd.movePointRight(2);
			return bd.longValue();
		}
		return 0L;
	}

}