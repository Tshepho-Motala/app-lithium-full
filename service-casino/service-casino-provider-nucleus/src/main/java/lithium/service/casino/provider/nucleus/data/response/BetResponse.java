package lithium.service.casino.provider.nucleus.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

@XmlType(propOrder = {"result", "code", "description", "extSystemTransactionId", "balanceCents", "bonusBet", "bonusWin"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BetResponse extends Response {
	@XmlElement(name="EXTSYSTEMTRANSACTIONID")
	private String extSystemTransactionId;
	@XmlElement(name="BALANCE")
	private Long balanceCents;
	@XmlElement(name="BONUSBET")
	private Long bonusBet;
	@XmlElement(name="BONUSWIN")
	private Long bonusWin;
	
	protected BetResponse() {};
	
	public BetResponse(String code, String result) {
		super(code, result);
	}
	
	public BetResponse(String extSystemTransactionId, Long balanceCents, Long bonusBet, Long bonusWin) {
		super();
		this.extSystemTransactionId = extSystemTransactionId;
		this.balanceCents = balanceCents;
		this.bonusBet = bonusBet;
		this.bonusWin = bonusWin;
	}
	
	public String getExtSystemTransactionId() {
		return extSystemTransactionId;
	}
	
	public void setExtSystemTransactionId(String extSystemTransactionId) {
		this.extSystemTransactionId = extSystemTransactionId;
	}
	
	public Long getBalanceCents() {
		return balanceCents;
	}
	
	public void setBalanceCents(Long balanceCents) {
		this.balanceCents = balanceCents;
	}
	
	public Long getBonusBet() {
		return bonusBet;
	}
	
	public void setBonusBet(Long bonusBet) {
		this.bonusBet = bonusBet;
	}
	
	public Long getBonusWin() {
		return bonusWin;
	}
	
	public void setBonusWin(Long bonusWin) {
		this.bonusWin = bonusWin;
	}
}