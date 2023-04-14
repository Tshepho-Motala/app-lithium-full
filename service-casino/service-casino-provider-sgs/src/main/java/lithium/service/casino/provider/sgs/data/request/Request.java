package lithium.service.casino.provider.sgs.data.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Root elemet for requests from sgs system
 * @author Chris
 *
 */
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="pkt")
public class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="methodcall")
	private MethodCall methodCall;

	public MethodCall getMethodCall() {
		return methodCall;
	}

	public void setMethodCall(MethodCall methodCall) {
		this.methodCall = methodCall;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
