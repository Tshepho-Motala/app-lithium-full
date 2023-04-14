
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EPaymentTransactionResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPaymentTransactionResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentReference" type="{}DebtorReferenceSimpleType"/>
 *         &lt;element name="DebtorReference" type="{}DebtorReferenceSimpleType"/>
 *         &lt;element name="TransactionID" type="{}TransactionIDType" minOccurs="0"/>
 *         &lt;element name="DebtorAdditionalData" type="{}DebtorAdditionalDataComplexType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="TransactionURL" type="{}URLSimpleType"/>
 *             &lt;element name="ShortTransactionURL" type="{}URLSimpleType" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="Error" type="{}ErrorComplexType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="entranceCode" use="required" type="{}EntranceCodeSimpleType" />
 *       &lt;attribute name="brandID" type="{}RelaxedIdentifierSimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPaymentTransactionResponseType", propOrder = {
    "paymentReference",
    "debtorReference",
    "transactionID",
    "debtorAdditionalData",
    "transactionURL",
    "shortTransactionURL",
    "error"
})
public class EPaymentTransactionResponseType {

    @XmlElement(name = "PaymentReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentReference;
    @XmlElement(name = "DebtorReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorReference;
    @XmlElement(name = "TransactionID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String transactionID;
    @XmlElement(name = "DebtorAdditionalData")
    protected DebtorAdditionalDataComplexType debtorAdditionalData;
    @XmlElement(name = "TransactionURL")
    @XmlSchemaType(name = "anyURI")
    protected String transactionURL;
    @XmlElement(name = "ShortTransactionURL")
    @XmlSchemaType(name = "anyURI")
    protected String shortTransactionURL;
    @XmlElement(name = "Error")
    protected ErrorComplexType error;
    @XmlAttribute(name = "entranceCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String entranceCode;
    @XmlAttribute(name = "brandID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String brandID;

    /**
     * Gets the value of the paymentReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentReference() {
        return paymentReference;
    }

    /**
     * Sets the value of the paymentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentReference(String value) {
        this.paymentReference = value;
    }

    /**
     * Gets the value of the debtorReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebtorReference() {
        return debtorReference;
    }

    /**
     * Sets the value of the debtorReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebtorReference(String value) {
        this.debtorReference = value;
    }

    /**
     * Gets the value of the transactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the value of the transactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionID(String value) {
        this.transactionID = value;
    }

    /**
     * Gets the value of the debtorAdditionalData property.
     * 
     * @return
     *     possible object is
     *     {@link DebtorAdditionalDataComplexType }
     *     
     */
    public DebtorAdditionalDataComplexType getDebtorAdditionalData() {
        return debtorAdditionalData;
    }

    /**
     * Sets the value of the debtorAdditionalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebtorAdditionalDataComplexType }
     *     
     */
    public void setDebtorAdditionalData(DebtorAdditionalDataComplexType value) {
        this.debtorAdditionalData = value;
    }

    /**
     * Gets the value of the transactionURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionURL() {
        return transactionURL;
    }

    /**
     * Sets the value of the transactionURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionURL(String value) {
        this.transactionURL = value;
    }

    /**
     * Gets the value of the shortTransactionURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortTransactionURL() {
        return shortTransactionURL;
    }

    /**
     * Sets the value of the shortTransactionURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortTransactionURL(String value) {
        this.shortTransactionURL = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorComplexType }
     *     
     */
    public ErrorComplexType getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorComplexType }
     *     
     */
    public void setError(ErrorComplexType value) {
        this.error = value;
    }

    /**
     * Gets the value of the entranceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntranceCode() {
        return entranceCode;
    }

    /**
     * Sets the value of the entranceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntranceCode(String value) {
        this.entranceCode = value;
    }

    /**
     * Gets the value of the brandID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrandID() {
        return brandID;
    }

    /**
     * Sets the value of the brandID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrandID(String value) {
        this.brandID = value;
    }

}
