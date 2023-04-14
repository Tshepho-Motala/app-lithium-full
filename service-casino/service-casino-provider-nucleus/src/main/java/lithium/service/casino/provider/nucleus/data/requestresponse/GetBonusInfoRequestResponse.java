package lithium.service.casino.provider.nucleus.data.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.data.request.GetBonusInfoRequest;
import lithium.service.casino.provider.nucleus.data.response.GetBonusInfoResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"request", "time", "response"})
@XmlRootElement(name = "NGSYSTEM")
public class GetBonusInfoRequestResponse extends RequestResponse {
	@XmlElement(name = "REQUEST")
	private GetBonusInfoRequest request;
	@XmlElement(name = "RESPONSE")
	private GetBonusInfoResponse response;
}