package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.CancelBonusRequest;
import lithium.service.casino.provider.nucleus.data.response.CancelBonusResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"request", "time", "response"})
@XmlRootElement(name = "NGSYSTEM")
public class CancelBonusRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private CancelBonusRequest request;
	@XmlElement(name = "RESPONSE")
	private CancelBonusResponse response;
}