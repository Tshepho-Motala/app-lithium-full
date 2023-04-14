package lithium.service.cashier.processor.wumg.directeller.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PaymentMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReceiverCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransactionAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransactionCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ExternalTraceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"username", "password", "paymentMethod", "transferType", "customerID", "receiverFirstName", "receiverLastName", "receiverPhoneNumber", "receiverCity", "receiverState", "receiverCountry",
	"transactionAmount", "transactionCurrency", "comments", "externalTraceID"
})
@XmlRootElement(name = "init_payout")
public class InitPayout {
	@XmlElement(name = "Username")
	protected String username;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "PaymentMethod")
	protected String paymentMethod;
	@XmlElement(name = "TransferType")
	protected String transferType;
	@XmlElement(name = "CustomerID")
	protected String customerID;
	@XmlElement(name = "ReceiverFirstName")
	protected String receiverFirstName;
	@XmlElement(name = "ReceiverLastName")
	protected String receiverLastName;
	@XmlElement(name = "ReceiverPhoneNumber")
	protected String receiverPhoneNumber;
	@XmlElement(name = "ReceiverCity")
	protected String receiverCity;
	@XmlElement(name = "ReceiverState")
	protected String receiverState;
	@XmlElement(name = "ReceiverCountry")
	protected String receiverCountry;
	@XmlElement(name = "TransactionAmount")
	protected String transactionAmount;
	@XmlElement(name = "TransactionCurrency")
	protected String transactionCurrency;
	@XmlElement(name = "Comments")
	protected String comments;
	@XmlElement(name = "ExternalTraceID")
	protected String externalTraceID;
}