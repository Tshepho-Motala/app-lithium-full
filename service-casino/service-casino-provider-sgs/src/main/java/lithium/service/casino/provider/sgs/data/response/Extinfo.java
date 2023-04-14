package lithium.service.casino.provider.sgs.data.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="extinfo")
public class Extinfo implements Serializable {
	private static final long serialVersionUID = 1L;
	

}