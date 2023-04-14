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
 *         &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransactionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MerchantCustomerPin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransactionAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ControlNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransferCharge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ExternalTraceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"username", "password", "customerID", "transactionID", "merchantCustomerPin", "senderFirstName", "senderLastName", "senderPhone", "senderCity", "senderState", "senderCountry", "transactionAmount",
	"controlNumber", "comments", "transferCharge", "externalTraceID"
})
@XmlRootElement(name = "confirm_deposit")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmDeposit {
	@XmlElement(name = "Username")
	protected String username;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "CustomerID")
	protected String customerID;
	@XmlElement(name = "TransactionID")
	protected String transactionID;
	@XmlElement(name = "MerchantCustomerPin")
	protected String merchantCustomerPin;
	@XmlElement(name = "SenderFirstName")
	protected String senderFirstName;
	@XmlElement(name = "SenderLastName")
	protected String senderLastName;
	@XmlElement(name = "SenderPhone")
	protected String senderPhone;
	@XmlElement(name = "SenderCity")
	protected String senderCity;
	@XmlElement(name = "SenderState")
	protected String senderState;
	@XmlElement(name = "SenderCountry")
	protected String senderCountry;
	@XmlElement(name = "TransactionAmount")
	protected String transactionAmount;
	@XmlElement(name = "ControlNumber")
	protected String controlNumber;
	@XmlElement(name = "Comments")
	protected String comments;
	@XmlElement(name = "TransferCharge")
	protected String transferCharge;
	@XmlElement(name = "ExternalTraceID")
	protected String externalTraceID;
}