package lithium.service.casino.provider.betsoft.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.data.request.BalanceRequest;
import lithium.service.casino.provider.betsoft.data.response.BalanceResponse;
import lombok.ToString;

@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BalanceRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private BalanceRequest request;
	@XmlElement(name = "RESPONSE")
	private BalanceResponse response;
	
	protected BalanceRequestResponse() {};
	
	public BalanceRequestResponse(BalanceRequest request, BalanceResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public BalanceRequest getRequest() {
		return request;
	}
	
	public void setRequest(BalanceRequest request) {
		this.request = request;
	}
	
	public BalanceResponse getResponse() {
		return response;
	}
	
	public void setResponse(BalanceResponse response) {
		this.response = response;
	}
}