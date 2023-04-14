package lithium.service.casino.provider.sgs.data.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Contains additional parameters lithium might require from SGS in requests
 *  
 * @author Chris
 *
 */
@EqualsAndHashCode(callSuper = false)
//@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
//@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class Extinfo implements Serializable {
	private static final long serialVersionUID = 1L;
	

	public Extinfo(String loginName, String currency, String gameReference) {
		super();
		this.loginName = loginName;
		this.currency = currency;
		this.gameReference = gameReference;
	}

	@XmlAttribute(name="loginname", required=false)
	private String loginName;
	
	@XmlAttribute(name="currency", required=false)
	private String currency;
	
	@XmlAttribute(name="gamereference", required=false)
	private String gameReference;


	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getGameReference() {
		return gameReference;
	}

	public void setGameReference(String gameReference) {
		this.gameReference = gameReference;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}