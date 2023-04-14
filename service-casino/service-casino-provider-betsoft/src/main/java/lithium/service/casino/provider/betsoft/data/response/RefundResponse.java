package lithium.service.casino.provider.betsoft.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

@XmlType(propOrder = {"result", "code", "description", "extSystemTransactionId"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundResponse extends Response {
	@XmlElement(name="EXTSYSTEMTRANSACTIONID")
	private String extSystemTransactionId;
	
	protected RefundResponse() {};
	
	public RefundResponse(String code, String result) {
		super(code, result);
	}
	
	public RefundResponse(String extSystemTransactionId) {
		super();
		this.extSystemTransactionId = extSystemTransactionId;
	}
	
	public String getExtSystemTransactionId() {
		return extSystemTransactionId;
	}
	
	public void setExtSystemTransactionId(String extSystemTransactionId) {
		this.extSystemTransactionId = extSystemTransactionId;
	}
}