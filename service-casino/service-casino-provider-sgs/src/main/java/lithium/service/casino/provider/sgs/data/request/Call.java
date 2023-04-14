package lithium.service.casino.provider.sgs.data.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Contains the request parameters required to complete the call to Lithium
 * At the very least it contains a sequence number and token.
 * Sequence number is just some useless number from SGS aand token is the current user token the request pertains to
 *  
 * @author Chris
 *
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
//@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class Call implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name="seq")
	private String seq;
	
	@XmlAttribute(name="token")
	private String token;
	
	
	@XmlAttribute(name="playtype", required=false)
	private String playType;
	
	@XmlAttribute(name="gameid", required=false)
	private String gameId;
	
	@XmlAttribute(name="gamereference", required=false)
	private String gameReference;
	
	@XmlAttribute(name="actionid", required=false)
	private String actionId; //uid
	
	@XmlAttribute(name="amount", required=false)
	private Long amount;
	
	@XmlAttribute(name="start", required=false)
	private Boolean start;
	
	@XmlAttribute(name="finish", required=false)
	private Boolean finish;
	
	@XmlAttribute(name="offline", required=false)
	private Boolean offline;
	
	@XmlAttribute(name="currency", required=false)
	private String currency;

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPlayType() {
		return playType;
	}

	public void setPlayType(String playType) {
		this.playType = playType;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameReference() {
		return gameReference;
	}

	public void setGameReference(String gameReference) {
		this.gameReference = gameReference;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Boolean getStart() {
		return start;
	}

	public void setStart(Boolean start) {
		this.start = start;
	}

	public Boolean getFinish() {
		return finish;
	}

	public void setFinish(Boolean finish) {
		this.finish = finish;
	}

	public Boolean getOffline() {
		return offline;
	}

	public void setOffline(Boolean offline) {
		this.offline = offline;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}