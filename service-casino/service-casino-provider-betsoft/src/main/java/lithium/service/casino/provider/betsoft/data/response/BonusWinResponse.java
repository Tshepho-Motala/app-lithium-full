package lithium.service.casino.provider.betsoft.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

@XmlType(propOrder = {"result", "code", "description", "balanceCents"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BonusWinResponse extends Response {
	@XmlElement(name="BALANCE")
	private Long balanceCents;
	
	protected BonusWinResponse() {};
	
	public BonusWinResponse(String code, String result) {
		super(code, result);
	}
	
	public BonusWinResponse(Long balanceCents) {
		super();
		this.balanceCents = balanceCents;
	}
	
	public Long getBalanceCents() {
		return balanceCents;
	}
	
	public void setBalanceCents(Long balanceCents) {
		this.balanceCents = balanceCents;
	}
}