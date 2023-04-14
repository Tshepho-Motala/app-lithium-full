
package lithium.service.cashier.processor.bluem.api.data;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EPaymentInterfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPaymentInterfaceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="PaymentTransactionRequest" type="{}EPaymentTransactionRequestType" maxOccurs="unbounded"/>
 *         &lt;element name="PaymentBatchResponse" type="{}EPaymentBatchResponseType" minOccurs="0"/>
 *         &lt;element name="PaymentTransactionResponse" type="{}EPaymentTransactionResponseType" maxOccurs="unbounded"/>
 *         &lt;element name="PaymentStatusRequest" type="{}EPaymentStatusRequestType" minOccurs="0"/>
 *         &lt;element name="PaymentStatusUpdate" type="{}EPaymentStatusUpdateType" maxOccurs="unbounded"/>
 *         &lt;element name="PaymentErrorResponse" type="{}PaymentErrorResponseType" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{}HeaderAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "EPaymentInterface")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPaymentInterface")
public class EPaymentInterfaceType {
    @XmlElement(name = "PaymentTransactionRequest")
    protected EPaymentTransactionRequestType paymentTransactionRequest;
    @XmlElement(name = "PaymentBatchResponse")
    protected EPaymentBatchResponseType paymentBatchResponse;
    @XmlElement(name = "PaymentTransactionResponse")
    protected EPaymentTransactionResponseType paymentTransactionResponse;
    @XmlElement(name = "PaymentStatusRequest")
    protected EPaymentStatusRequestType paymentStatusRequest;
    @XmlElement(name = "PaymentStatusUpdate")
    protected EPaymentStatusUpdateType paymentStatusUpdate;
    @XmlElement(name = "PaymentErrorResponse")
    protected PaymentErrorResponseType paymentErrorResponse;

    protected List<Object> paymentTransactionRequestOrPaymentBatchResponseOrPaymentTransactionResponse;
    @XmlAttribute(name = "mode", required = true)
    protected ModeSimpleType mode;
    @XmlAttribute(name = "batchID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String batchID;
    @XmlAttribute(name = "senderID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String senderID;
    @XmlAttribute(name = "version")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String version;
    @XmlAttribute(name = "createDateTime", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String createDateTime;
    @XmlAttribute(name = "messageCount", required = true)
    protected int messageCount;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

    public EPaymentTransactionRequestType getPaymentTransactionRequest() {
        return paymentTransactionRequest;
    }

    public void setPaymentTransactionRequest(EPaymentTransactionRequestType paymentTransactionRequest) {
        this.paymentTransactionRequest = paymentTransactionRequest;
    }

    public EPaymentBatchResponseType getPaymentBatchResponse() {
        return paymentBatchResponse;
    }
    public void setPaymentBatchResponse(EPaymentBatchResponseType paymentBatchResponse) {
        this.paymentBatchResponse = paymentBatchResponse;
    }

    public EPaymentTransactionResponseType getPaymentTransactionResponse() {
        return paymentTransactionResponse;
    }

    public void setPaymentTransactionResponse(EPaymentTransactionResponseType paymentTransactionResponse) {
        this.paymentTransactionResponse = paymentTransactionResponse;
    }

    public EPaymentStatusRequestType getPaymentStatusRequest() {
        return paymentStatusRequest;
    }

    public void setPaymentStatusRequest(EPaymentStatusRequestType paymentStatusRequest) {
        this.paymentStatusRequest = paymentStatusRequest;
    }

    public EPaymentStatusUpdateType getPaymentStatusUpdate() {
        return paymentStatusUpdate;
    }

    public void getPaymentStatusUpdate(EPaymentStatusUpdateType paymentStatusUpdate) {
        this.paymentStatusUpdate = paymentStatusUpdate;
    }

    public PaymentErrorResponseType gatPaymentErrorResponse() {
        return paymentErrorResponse;
    }

    public void setPaymentErrorResponse(PaymentErrorResponseType paymentErrorResponse) {
        this.paymentErrorResponse = paymentErrorResponse;
    }
    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link ModeSimpleType }
     *     
     */
    public ModeSimpleType getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModeSimpleType }
     *     
     */
    public void setMode(ModeSimpleType value) {
        this.mode = value;
    }

    /**
     * Gets the value of the batchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchID() {
        return batchID;
    }

    /**
     * Sets the value of the batchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchID(String value) {
        this.batchID = value;
    }

    /**
     * Gets the value of the senderID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * Sets the value of the senderID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderID(String value) {
        this.senderID = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        if (version == null) {
            return "1.0";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the createDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateDateTime() {
        return createDateTime;
    }

    /**
     * Sets the value of the createDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateDateTime(String value) {
        this.createDateTime = value;
    }

    /**
     * Gets the value of the messageCount property.
     * 
     */
    public int getMessageCount() {
        return messageCount;
    }

    /**
     * Sets the value of the messageCount property.
     * 
     */
    public void setMessageCount(int value) {
        this.messageCount = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
