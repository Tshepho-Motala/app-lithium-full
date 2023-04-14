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
public class GetBalanceResult<E extends Extinfo> extends Result<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public GetBalanceResult(String seq, String token, String balance, String bonusbalance) {
		super(seq, token);
		this.balance = balance;
		this.bonusbalance = bonusbalance;
	}
	
	@XmlAttribute(name="balance")
	private String balance;
	
	@XmlAttribute(name="bonusbalance")
	private String bonusbalance;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}