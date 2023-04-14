
package lithium.service.cashier.processor.bluem.api.data;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EPaymentStatusUpdateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPaymentStatusUpdateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CreationDateTime" type="{}DateTimeSimpleType"/>
 *         &lt;element name="PaymentReference" type="{}PaymentReferenceSimpleType"/>
 *         &lt;element name="DebtorReference" type="{}DebtorReferenceSimpleType"/>
 *         &lt;element name="TransactionID" type="{}TransactionIDType"/>
 *         &lt;element name="Status" type="{}StatusSimpleType"/>
 *         &lt;element name="Amount" type="{}AmountSimpleType" minOccurs="0"/>
 *         &lt;element name="AmountPaid" type="{}AmountSimpleType" minOccurs="0"/>
 *         &lt;element name="Currency" type="{}CurrencySimpleType" minOccurs="0"/>
 *         &lt;element name="PaymentMethod" type="{}PaymentMethodsSimpleType" minOccurs="0"/>
 *         &lt;element name="PaymentMethodDetails" type="{}PaymentMethodDetailsComplexType" minOccurs="0"/>
 *         &lt;element name="Error" type="{}ErrorComplexType" minOccurs="0"/>
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
@XmlType(name = "EPaymentStatusUpdateType", propOrder = {
    "creationDateTime",
    "paymentReference",
    "debtorReference",
    "transactionID",
    "status",
    "amount",
    "amountPaid",
    "currency",
    "paymentMethod",
    "paymentMethodDetails",
    "error"
})
public class EPaymentStatusUpdateType {

    @XmlElement(name = "CreationDateTime", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String creationDateTime;
    @XmlElement(name = "PaymentReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentReference;
    @XmlElement(name = "DebtorReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorReference;
    @XmlElement(name = "TransactionID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String transactionID;
    @XmlElement(name = "Status", required = true)
    @XmlSchemaType(name = "token")
    protected StatusSimpleType status;
    @XmlElement(name = "Amount")
    protected BigDecimal amount;
    @XmlElement(name = "AmountPaid")
    protected BigDecimal amountPaid;
    @XmlElement(name = "Currency", defaultValue = "EUR")
    @XmlSchemaType(name = "token")
    protected CurrencySimpleType currency;
    @XmlElement(name = "PaymentMethod")
    @XmlSchemaType(name = "token")
    protected PaymentMethodsSimpleType paymentMethod;
    @XmlElement(name = "PaymentMethodDetails")
    protected PaymentMethodDetailsComplexType paymentMethodDetails;
    @XmlElement(name = "Error")
    protected ErrorComplexType error;
    @XmlAttribute(name = "entranceCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String entranceCode;
    @XmlAttribute(name = "brandID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String brandID;

    /**
     * Gets the value of the creationDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the value of the creationDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationDateTime(String value) {
        this.creationDateTime = value;
    }

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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusSimpleType }
     *     
     */
    public StatusSimpleType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusSimpleType }
     *     
     */
    public void setStatus(StatusSimpleType value) {
        this.status = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the amountPaid property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    /**
     * Sets the value of the amountPaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmountPaid(BigDecimal value) {
        this.amountPaid = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencySimpleType }
     *     
     */
    public CurrencySimpleType getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencySimpleType }
     *     
     */
    public void setCurrency(CurrencySimpleType value) {
        this.currency = value;
    }

    /**
     * Gets the value of the paymentMethod property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodsSimpleType }
     *     
     */
    public PaymentMethodsSimpleType getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the value of the paymentMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodsSimpleType }
     *     
     */
    public void setPaymentMethod(PaymentMethodsSimpleType value) {
        this.paymentMethod = value;
    }

    /**
     * Gets the value of the paymentMethodDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodDetailsComplexType }
     *     
     */
    public PaymentMethodDetailsComplexType getPaymentMethodDetails() {
        return paymentMethodDetails;
    }

    /**
     * Sets the value of the paymentMethodDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodDetailsComplexType }
     *     
     */
    public void setPaymentMethodDetails(PaymentMethodDetailsComplexType value) {
        this.paymentMethodDetails = value;
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
