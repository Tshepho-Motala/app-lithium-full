package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.BetRequest;
import lithium.service.casino.provider.nucleus.data.response.BetResponse;
import lombok.ToString;


@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BetRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private BetRequest request;
	@XmlElement(name = "RESPONSE")
	private BetResponse response;
	
	protected BetRequestResponse() {};
	
	public BetRequestResponse(BetRequest request, BetResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public BetRequest getRequest() {
		return request;
	}
	
	public void setRequest(BetRequest request) {
		this.request = request;
	}
	
	public BetResponse getResponse() {
		return response;
	}
	
	public void setResponse(BetResponse response) {
		this.response = response;
	}
}