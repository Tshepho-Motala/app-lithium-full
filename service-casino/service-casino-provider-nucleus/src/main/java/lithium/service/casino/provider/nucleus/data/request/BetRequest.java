package lithium.service.casino.provider.nucleus.data.request;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.util.HashCalculator;
import lombok.ToString;


@XmlType(propOrder = {"userId", "bet", "win", "roundId", "gameId", "roundFinished", "hash", "gameSessionId", "negativeBet"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BetRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	@XmlElement(name="BET")
	private String bet;
	@XmlElement(name="WIN")
	private String win;
	@XmlElement(name="ROUNDID")
	private String roundId;
	@XmlElement(name="GAMEID")
	private Integer gameId;
	@XmlElement(name="ISROUNDFINISHED")
	private Boolean roundFinished;
	@XmlElement(name="GAMESESSIONID")
	private String gameSessionId;
	@XmlElement(name="NEGATIVEBET")
	private Integer negativeBet;
	
	protected BetRequest() {}
	
	public BetRequest(String userId, String bet, String win, String roundId, Integer gameId, Boolean roundFinished, String gameSessionId, Integer negativeBet) {
		super();
		this.userId = userId;
		this.bet = bet;
		this.win = win;
		this.roundId = roundId;
		this.gameId = gameId;
		this.roundFinished = roundFinished;
		this.gameSessionId = gameSessionId;
		this.negativeBet = negativeBet;
	}
	
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		
		hashCalc.addItem(userId);
		hashCalc.addItem(bet);
		hashCalc.addItem(win);
		hashCalc.addItem(roundFinished);
		hashCalc.addItem(roundId);
		hashCalc.addItem(gameId);
		
		return hashCalc.calculateHash();
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getBet() {
		return bet;
	}
	
	public void setBet(String bet) {
		this.bet = bet;
	}
	
	public String getWin() {
		return win;
	}
	
	public void setWin(String win) {
		this.win = win;
	}
	
	public String getRoundId() {
		return roundId;
	}
	
	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}
	
	public Integer getGameId() {
		return gameId;
	}
	
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	
	public Boolean getRoundFinished() {
		return roundFinished;
	}
	
	public void setRoundFinished(Boolean roundFinished) {
		this.roundFinished = roundFinished;
	}
	
	public String getGameSessionId() {
		return gameSessionId;
	}
	
	public void setGameSessionId(String gameSessionId) {
		this.gameSessionId = gameSessionId;
	}
	
	public Integer getNegativeBet() {
		return negativeBet;
	}
	
	public void setNegativeBet(Integer negativeBet) {
		this.negativeBet = negativeBet;
	}
	
	public Long getBetCents() {
		if (bet == null) return null;
		StringTokenizer st = new StringTokenizer(bet, "|");
		if (!st.hasMoreElements()) return null;
		return Long.parseLong(st.nextToken());
	}
	
	public Long getBetTransactionId() {
		if (bet == null) return null;
		StringTokenizer st = new StringTokenizer(bet, "|");
		if (!st.hasMoreElements()) return null;
		st.nextToken();
		if (!st.hasMoreElements()) return null;
		return Long.parseLong(st.nextToken());
	}
	
	public Long getWinCents() {
		if (win == null) return null;
		StringTokenizer st = new StringTokenizer(win, "|");
		if (!st.hasMoreElements()) return null;
		Long cents = Long.parseLong(st.nextToken());
		//if (negativeBet != null) cents += negativeBet;
		return cents;
	}
	
	public Long getWinTransactionId() {
		if (win == null) return null;
		StringTokenizer st = new StringTokenizer(win, "|");
		if (!st.hasMoreElements()) return null;
		st.nextToken();
		if (!st.hasMoreElements()) return null;
		return Long.parseLong(st.nextToken());
	}
	
	@Override
	public String toString() {
		return "BetRequest [userId=" + userId + ", bet=" + bet + ", win=" + win
				+ ", roundId=" + roundId + ", gameId=" + gameId
				+ ", roundFinished=" + roundFinished + ", gameSessionId="
				+ gameSessionId + ", negativeBet=" + negativeBet + "] " + super.toString();
	};

	@Override
	public String toHttpParameterMapString() {
		StringBuffer sb = new StringBuffer();

		appender("hash", getHash(), sb);
		appender("userId", getUserId(), sb);
		appender("bet", getBet(), sb);
		appender("win", getWin(), sb);
		appender("roundId", getRoundId(), sb);
		appender("gameId", getGameId(), sb);
		appender("isRoundFinished", getRoundFinished(), sb);
		appender("gameSessionId", getGameSessionId(), sb);
		appender("negativeBet", getNegativeBet(), sb);
		return sb.substring(0, sb.length()-1);
	}
}