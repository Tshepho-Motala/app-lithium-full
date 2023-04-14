package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.AccountInfoRequest;
import lithium.service.casino.provider.nucleus.data.response.AccountInfoResponse;
import lombok.ToString;


@XmlRootElement(name = "EXTSYSTEM")
@XmlType(propOrder = {"request", "time", "response"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountInfoRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private AccountInfoRequest request;
	@XmlElement(name = "RESPONSE")
	private AccountInfoResponse response;

	protected AccountInfoRequestResponse() {};
	
	public AccountInfoRequestResponse(AccountInfoRequest request, AccountInfoResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public AccountInfoRequest getRequest() {
		return request;
	}

	public void setRequest(AccountInfoRequest request) {
		this.request = request;
	}
	
	public AccountInfoResponse getResponse() {
		return response;
	}

	public void setResponse(AccountInfoResponse response) {
		this.response = response;
	}

}
