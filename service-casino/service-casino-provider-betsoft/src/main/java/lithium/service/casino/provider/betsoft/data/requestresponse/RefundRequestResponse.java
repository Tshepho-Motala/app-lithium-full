package lithium.service.casino.provider.betsoft.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.data.request.RefundRequest;
import lithium.service.casino.provider.betsoft.data.response.RefundResponse;
import lombok.ToString;


@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private RefundRequest request;
	@XmlElement(name = "RESPONSE")
	private RefundResponse response;

	protected RefundRequestResponse() {};
	
	public RefundRequestResponse(RefundRequest request, RefundResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public RefundRequest getRequest() {
		return request;
	}
	
	public void setRequest(RefundRequest request) {
		this.request = request;
	}
	
	public RefundResponse getResponse() {
		return response;
	}
	
	public void setResponse(RefundResponse response) {
		this.response = response;
	}
}