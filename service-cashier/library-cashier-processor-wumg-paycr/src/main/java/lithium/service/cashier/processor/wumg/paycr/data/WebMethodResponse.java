package lithium.service.cashier.processor.wumg.paycr.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@ToString
public class WebMethodResponse {

	@XmlElement(name="methodName")
	private String methodName;
	@XmlElement(name="responseStatus")
	private String responseStatus;
	@XmlElement(name="responseDescription")
	private String responseDescription;
	@XmlElement(name="transactionStatusId")
	private String transactionStatusId;
	@XmlElement(name="transactionStatusDescription")
	private String transactionStatusDescription;
	@XmlElement(name="transactionAmount")
	private String transactionAmount;
	@XmlElement(name="tempCommitedid")
	private String tempCommitedId;
	@XmlElement(name="receiverId")
	private String receiverId;
	@XmlElement(name="receiverName")
	private String receiverName;
	@XmlElement(name="city")
	private String city;
	@XmlElement(name="state")
	private String state;
	@XmlElement(name="country")
	private String country;
	@XmlElement(name="transactionId")
	private String transactionId;
	@XmlElement(name="exttransactionId")
	private String extTransactionId;
	@XmlElement(name="Comments")
	private String comments;
	
}
