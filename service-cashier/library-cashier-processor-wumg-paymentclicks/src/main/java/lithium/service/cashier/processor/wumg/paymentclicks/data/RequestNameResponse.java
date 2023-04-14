package lithium.service.cashier.processor.wumg.paymentclicks.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@XmlRootElement(name="worldonlinetransfers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@ToString
public class RequestNameResponse {

	@XmlAttribute
	private String version = "1.0";
	
	@XmlElement(name="name_id")
	private String nameId; 
	
	@XmlElement(name="processor")
	private String processor;
	
	@XmlElement(name="sender")
	private String sender;
	
	@XmlElement(name="account")
	private String account;
	
	@XmlElement(name="receiver_name")
	private String receiverName;

	@XmlElement(name="receiver_country")
	private String receiverCountry;

	@XmlElement(name="receiver_city")
	private String receiverCity;

	@XmlElement(name="status")
	private String status;
	
	@XmlElement(name="error_message")
	private String errorMessage;
}
