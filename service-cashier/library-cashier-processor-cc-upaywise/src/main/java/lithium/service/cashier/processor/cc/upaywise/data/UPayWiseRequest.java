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
@XmlRootElement(name="request")
public class UPayWiseRequest {
	@XmlElement(name="terminalid")
	private String terminalId;
	@XmlElement
	private String password;
	@XmlElement
	private String action;
	@XmlElement
	private String card;
	@XmlElement
	private String cvv2;
	@XmlElement
	private String expYear;
	@XmlElement
	private String expMonth;
	@XmlElement
	private String member;
	@XmlElement
	private String currencyCode;
	@XmlElement
	private String address;
	@XmlElement
	private String city;
	@XmlElement(name="statecode")
	private String stateCode;
	@XmlElement
	private String zip;
	@XmlElement(name="CountryCode")
	private String countryCode;
	@XmlElement
	private String email;
	@XmlElement
	private String amount;
	@XmlElement(name="trackid")
	private String merchantTrackId;
	@XmlElement
	private String tranMessageId;
	@XmlElement
	private String merchantIp;
	@XmlElement
	private String customerIp;
	@XmlElement
	private String udf1;
	@XmlElement
	private String udf2;
	@XmlElement
	private String udf3;
	@XmlElement
	private String udf4;
}