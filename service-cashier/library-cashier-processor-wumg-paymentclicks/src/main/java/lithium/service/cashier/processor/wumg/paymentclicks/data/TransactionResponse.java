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
public class TransactionResponse {

	@XmlAttribute
	private String version = "1.0";
	
	@XmlElement(name="id")
	private String id; 

	@XmlElement(name="type")
	private String type; 
	
	@XmlElement(name="processor")
	private String processor;

	@XmlElement(name="pin")
	private String pin;

	@XmlElement(name="amount")
	private String amount;

	@XmlElement(name="fee")
	private String fee;

	@XmlElement(name="charge")
	private String charge;
	
	@XmlElement(name="control_number")
	private String controlNumber;
	
	@XmlElement(name="transaction_date")
	private String transactionDate;
	
	@XmlElement(name="processed_date")
	private String processedDate;
	
	@XmlElement(name="sender_name")
	private String senderName;
	
	@XmlElement(name="receiver_name")
	private String receiverName;
	
	@XmlElement(name="sender_state")
	private String senderState;
	
	@XmlElement(name="sender_country")
	private String senderCountry;

	@XmlElement(name="receiver_city")
	private String receiverCity;

	@XmlElement(name="receiver_country")
	private String receiverCountry;

	@XmlElement(name="comments")
	private String comments;

	@XmlElement(name="feedback")
	private String feedback;

	@XmlElement(name="status")
	private String status;

	@XmlElement(name="error_message")
	private String errorMessage;
}
