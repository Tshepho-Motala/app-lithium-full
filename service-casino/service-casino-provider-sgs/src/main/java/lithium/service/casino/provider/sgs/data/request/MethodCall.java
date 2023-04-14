package lithium.service.casino.provider.sgs.data.request;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lithium.service.casino.provider.sgs.util.DateXmlAdapter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Wrapper for all request calls from SGS.
 * @author Chris
 *
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
//@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class MethodCall implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name="name")
	private String name;
	
	//TODO: Map to proper timestamp type
	@XmlAttribute(name="timestamp")
	@XmlJavaTypeAdapter(value = DateXmlAdapter.class)
	private Date timestamp;
	
	@XmlAttribute(name="system")
	private String system;
	
	@XmlElement(name="auth")
	private Auth auth;
	
	@XmlElement(name="call")
	private Call call;
	
	@XmlElement(name="extinfo")
	private Extinfo extinfo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth) {
		this.auth = auth;
	}

	public Call getCall() {
		return call;
	}

	public void setCall(Call call) {
		this.call = call;
	}

	public Extinfo getExtinfo() {
		return extinfo;
	}

	public void setExtinfo(Extinfo extinfo) {
		this.extinfo = extinfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}