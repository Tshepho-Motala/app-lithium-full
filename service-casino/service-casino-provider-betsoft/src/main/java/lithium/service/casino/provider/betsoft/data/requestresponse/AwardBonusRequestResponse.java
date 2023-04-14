package lithium.service.casino.provider.betsoft.data.requestresponse;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.data.request.AwardBonusRequest;
import lithium.service.casino.provider.betsoft.data.response.AwardBonusResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"request", "time", "response"})
@XmlRootElement(name = "BSGSYSTEM")
public class AwardBonusRequestResponse extends RequestResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "REQUEST")
	private AwardBonusRequest request;
	@XmlElement(name = "RESPONSE")
	private AwardBonusResponse response;
}