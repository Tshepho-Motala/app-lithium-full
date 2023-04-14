package lithium.service.casino.provider.betsoft.data.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.ToString;

@ToString
@XmlTransient
public abstract class Request {
	@XmlElement(name="HASH")
	private String hash;
	
	protected Request() {};
	
	public Request(String hash) {
		this.hash = hash;
	}
	
	public abstract String calculateHash(String password);
	
	public void storeCalculatedHash(String password) {
		hash = calculateHash(password);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String toHttpParameterMapString() { return ""; };


	protected <E> void appender(final String paramName, final E paramValue, StringBuffer sb) {
		if (paramValue == null) return;

		sb.append(paramName);
		sb.append("=");
		sb.append(paramValue.toString());
		sb.append("&");
	}

}
