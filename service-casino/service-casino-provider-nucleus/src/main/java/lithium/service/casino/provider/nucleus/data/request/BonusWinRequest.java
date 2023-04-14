package lithium.service.casino.provider.nucleus.data.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import lithium.service.casino.provider.nucleus.util.HashCalculator;

@XmlType(propOrder = {"userId", "bonusId", "amount", "transactionId", "hash"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BonusWinRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	@XmlElement(name="BONUSID")
	private Integer bonusId;
	@XmlElement(name="AMOUNT")
	private Long amount;
	@XmlElement(name="TRANSACTIONID")
	private String transactionId;
	
	protected BonusWinRequest() {};
	
	public BonusWinRequest(String userId, Integer bonusId, Long amount, String transactionId) {
		super();
		this.userId = userId;
		this.bonusId = bonusId;
		this.amount = amount;
		this.transactionId = transactionId;
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
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toHttpParameterMapString() {
		StringBuffer sb = new StringBuffer();

		appender("hash", getHash(), sb);
		appender("userId", getUserId(), sb);
		appender("bonusId", getBonusId(), sb);
		appender("amount", getAmount(), sb);
		appender("transactionId", getTransactionId(), sb);
		return sb.substring(0, sb.length()-1);
	}
}