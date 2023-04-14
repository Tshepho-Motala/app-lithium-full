package lithium.service.cashier.processor.cc.upaywise.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="response")
public class UPayWiseResponse {
	@XmlElement
	private String result;
	@XmlElement(name="resultcode")
	private String responseCode;
	@XmlElement(name="authcode")
	private String authCode;
	@XmlElement(name="ECI")
	private String eci;
	@XmlElement(name="tranid")
	private String tranId;
	@XmlElement(name="trackid")
	private String trackId;
	@XmlElement(name="terminalid")
	private String terminalId;
	@XmlElement(name="RRN")
	private String rrn;
	@XmlElement
	private String udf1;
	@XmlElement
	private String udf2;
	@XmlElement
	private String udf3;
	@XmlElement
	private String udf4;
	@XmlElement
	private String udf5;
	@XmlElement
	private String targetUrl;
	@XmlElement
	private String payId;
}