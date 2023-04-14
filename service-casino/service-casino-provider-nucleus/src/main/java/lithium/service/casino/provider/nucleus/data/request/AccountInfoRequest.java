package lithium.service.casino.provider.nucleus.data.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.util.HashCalculator;
import lombok.ToString;

@XmlType(propOrder = {"userId", "hash"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountInfoRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	
	protected AccountInfoRequest() {};
	
	public AccountInfoRequest(String userId) {
		super();
		this.userId = userId;
	}
	
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(userId);
		return hashCalc.calculateHash();
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
}