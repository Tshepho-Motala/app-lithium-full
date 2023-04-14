package lithium.service.casino.provider.betsoft.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.data.request.BonusReleaseRequest;
import lithium.service.casino.provider.betsoft.data.response.BonusReleaseResponse;
import lombok.ToString;


@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BonusReleaseRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private BonusReleaseRequest request;
	@XmlElement(name = "RESPONSE")
	private BonusReleaseResponse response;
	
	protected BonusReleaseRequestResponse() {};
	
	public BonusReleaseRequestResponse(BonusReleaseRequest request, BonusReleaseResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public BonusReleaseRequest getRequest() {
		return request;
	}
	
	public void setRequest(BonusReleaseRequest request) {
		this.request = request;
	}
	
	public BonusReleaseResponse getResponse() {
		return response;
	}
	
	public void setResponse(BonusReleaseResponse response) {
		this.response = response;
	}
}