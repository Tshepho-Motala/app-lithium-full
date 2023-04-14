package lithium.service.casino.provider.sgs.data.response;

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
public class Response<R extends Result<? extends Extinfo>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="methodresponse")
	private MethodResponse<R> methodResponse;
}
