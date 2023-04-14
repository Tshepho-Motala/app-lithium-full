package lithium.service.casino.provider.sgs.data.response;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( {GetBalanceResult.class, LoginResult.class, PlayResult.class, EndgameResult.class, RefreshtokenResult.class, Extinfo.class} )
public class MethodResponse<R extends Result<? extends Extinfo>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlAttribute(name="timestamp")
	@XmlJavaTypeAdapter(value = DateXmlAdapter.class)
	private Date timestamp;
	
	@XmlAnyElement(lax=true)
	private R result;
}