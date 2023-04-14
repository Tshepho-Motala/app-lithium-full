package lithium.service.casino.provider.nucleus.data.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import lithium.service.casino.provider.nucleus.util.HashCalculator;

@XmlType(propOrder = {"userId", "casinoTransactionId", "hash"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	@XmlElement(name="CASINOTRANSACTIONID")
	private Long casinoTransactionId;
	
	protected RefundRequest() {}
	
	public RefundRequest(String userId, Long casinoTransactionId) {
		super();
		this.userId = userId;
		this.casinoTransactionId = casinoTransactionId;
	}
	
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		
		hashCalc.addItem(userId);
		hashCalc.addItem(casinoTransactionId);
		
		return hashCalc.calculateHash();
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Long getCasinoTransactionId() {
		return casinoTransactionId;
	}
	
	public void setCasinoTransactionId(Long casinoTransactionId) {
		this.casinoTransactionId = casinoTransactionId;
	}

	@Override
	public String toHttpParameterMapString() {
		StringBuffer sb = new StringBuffer();

		appender("hash", getHash(), sb);
		appender("userId", getUserId(), sb);
		appender("casinoTransactionId", getCasinoTransactionId(), sb);
		return sb.substring(0, sb.length()-1);
	}
}