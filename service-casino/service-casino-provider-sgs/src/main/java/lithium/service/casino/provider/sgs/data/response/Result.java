package lithium.service.casino.provider.sgs.data.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Contains the request parameters required to complete the call to Lithium
 * At the very least it contains a sequence number and token.
 * Sequence number is just some useless number from SGS aand token is the current user token the request pertains to
 *  
 * @author Chris
 *
 */
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="result")
@XmlSeeAlso( {Extinfo.class} )
public class Result<E extends Extinfo> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	public Result(String seq, String token) {
		super();
		this.seq = seq;
		this.token = token;
	}

	@XmlAttribute(name="seq")
	private String seq;
	
	@XmlAttribute(name="token")
	private String token;
	
	@XmlAttribute(name="errorcode", required=false)
	private String errorCode;
	
	@XmlAttribute(name="errordescription", required=false)
	private String errorDescription;

	@XmlAnyElement(lax=true)
	private E extinfo;
	
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public E getExtinfo() {
		return extinfo;
	}

	public void setExtinfo(E extinfo) {
		this.extinfo = extinfo;
	}
}