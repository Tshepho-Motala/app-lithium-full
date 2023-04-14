package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.BonusWinRequest;
import lithium.service.casino.provider.nucleus.data.response.BonusWinResponse;
import lombok.ToString;

@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BonusWinRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private BonusWinRequest request;
	@XmlElement(name = "RESPONSE")
	private BonusWinResponse response;
	
	protected BonusWinRequestResponse() {};
	
	public BonusWinRequestResponse(BonusWinRequest request, BonusWinResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public BonusWinRequest getRequest() {
		return request;
	}
	
	public void setRequest(BonusWinRequest request) {
		this.request = request;
	}
	
	public BonusWinResponse getResponse() {
		return response;
	}
	
	public void setResponse(BonusWinResponse response) {
		this.response = response;
	}
}