package lithium.service.casino.provider.nucleus.data.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.util.HashCalculator;
import lombok.ToString;

@XmlType(propOrder = {"token", "hash"})
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationRequest extends Request {
	@XmlElement(name="TOKEN")
	private String token;

	protected AuthenticationRequest() {};
	
	public AuthenticationRequest(String token) {
		super();
		this.token = token;
	}
	
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(token);
		return hashCalc.calculateHash();
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toHttpParameterMapString() {
		StringBuffer sb = new StringBuffer();

		appender("hash", getHash(), sb);
		appender("token", getToken(), sb);
		return sb.substring(0, sb.length()-1);
	}
}
