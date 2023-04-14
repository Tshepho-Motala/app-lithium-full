package lithium.service.casino.provider.betsoft.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.data.request.AuthenticationRequest;
import lithium.service.casino.provider.betsoft.data.response.AuthenticationResponse;
import lombok.ToString;



@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private AuthenticationRequest request;
	@XmlElement(name = "RESPONSE")
	private AuthenticationResponse response;

	protected AuthenticationRequestResponse() {};
	
	public AuthenticationRequestResponse(AuthenticationRequest request, AuthenticationResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public AuthenticationRequest getRequest() {
		return request;
	}

	public void setRequest(AuthenticationRequest request) {
		this.request = request;
	}
	
	public AuthenticationResponse getResponse() {
		return response;
	}

	public void setResponse(AuthenticationResponse response) {
		this.response = response;
	}

}
