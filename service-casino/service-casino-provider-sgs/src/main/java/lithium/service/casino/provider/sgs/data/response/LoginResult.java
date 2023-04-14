package lithium.service.casino.provider.sgs.data.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="result")
@XmlSeeAlso( {Extinfo.class} )
public class LoginResult<E extends Extinfo> extends Result<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public LoginResult(String seq, String token, String loginname, String currency, String country, String city,
			String balance, String bonusbalance, String wallet, String idnumber, String responsiblegaming,
			String regulatedmarket) {
		super(seq, token);
		this.loginname = loginname;
		this.currency = currency;
		this.country = country;
		this.city = city;
		this.balance = balance;
		this.bonusbalance = bonusbalance;
		this.wallet = wallet;
		this.idnumber = idnumber;
		this.responsiblegaming = responsiblegaming;
		this.regulatedmarket = regulatedmarket;
	}

	@XmlAttribute(name="loginname")
	private String loginname;
	
	@XmlAttribute(name="currency")
	private String currency;
	
	@XmlAttribute(name="country")
	private String country;
	
	@XmlAttribute(name="city")
	private String city;
	
	@XmlAttribute(name="balance")
	private String balance;
	
	@XmlAttribute(name="bonusbalance")
	private String bonusbalance;
	
	@XmlAttribute(name="wallet")
	private String wallet;
	
	@XmlAttribute(name="idnumber")
	private String idnumber;
	
	@XmlAttribute(name="responsiblegaming")
	private String responsiblegaming;
	
	@XmlAttribute(name="regulatedmarket")
	private String regulatedmarket;

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getBonusbalance() {
		return bonusbalance;
	}

	public void setBonusbalance(String bonusbalance) {
		this.bonusbalance = bonusbalance;
	}

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getResponsiblegaming() {
		return responsiblegaming;
	}

	public void setResponsiblegaming(String responsiblegaming) {
		this.responsiblegaming = responsiblegaming;
	}

	public String getRegulatedmarket() {
		return regulatedmarket;
	}

	public void setRegulatedmarket(String regulatedmarket) {
		this.regulatedmarket = regulatedmarket;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}