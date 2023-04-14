package lithium.service.casino.provider.betsoft.data.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.util.HashCalculator;
import lombok.ToString;

@XmlType(propOrder = {"userId", "bonusId", "amount", "hash"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BonusReleaseRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	@XmlElement(name="BONUSID")
	private Integer bonusId;
	@XmlElement(name="AMOUNT")
	private Long amount;
	
	protected BonusReleaseRequest() {};
	
	public BonusReleaseRequest(String userId, Integer bonusId, Long amount) {
		super();
		this.userId = userId;
		this.bonusId = bonusId;
		this.amount = amount;
	}
	
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(userId);
		hashCalc.addItem(bonusId);
		hashCalc.addItem(amount);
		return hashCalc.calculateHash();
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Integer getBonusId() {
		return bonusId;
	}
	
	public void setBonusId(Integer bonusId) {
		this.bonusId = bonusId;
	}
	
	public Long getAmount() {
		return amount;
	}
	
	public void setAmount(Long amount) {
		this.amount = amount;
	}
}