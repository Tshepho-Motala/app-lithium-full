package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.CheckBonusRequest;
import lithium.service.casino.provider.nucleus.data.response.CheckBonusResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlRootElement(name = "NGSYSTEM")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"request", "time", "response"})
public class CheckBonusRequestResponse extends RequestResponse {
	private static final long serialVersionUID = 2849871170865366014L;
	
	@XmlElement(name="REQUEST")
	private CheckBonusRequest request;
	@XmlElement(name="RESPONSE")
	private CheckBonusResponse response;
}
