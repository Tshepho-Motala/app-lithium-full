
package lithium.service.cashier.processor.bluem.api.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lithium.util.BigDecimalMoneyJsonSerializer;

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
 * <p>Java class for EPaymentTransactionRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPaymentTransactionRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentReference" type="{}PaymentReferenceSimpleType"/>
 *         &lt;element name="DebtorReference" type="{}DebtorReferenceSimpleType"/>
 *         &lt;element name="Description" type="{}DescriptionSimpleType"/>
 *         &lt;element name="SkinID" type="{}RelaxedIdentifierSimpleType" minOccurs="0"/>
 *         &lt;element name="Currency" type="{}CurrencySimpleType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;group ref="{}AmountPayable"/>
 *           &lt;element name="AmountArray" type="{}AmountArrayComplexType"/>
 *         &lt;/choice>
 *         &lt;element name="DueDateTime" type="{}DateTimeSimpleType"/>
 *         &lt;element name="DebtorReturnURL" type="{}DebtorReturnURLComplexType" minOccurs="0"/>
 *         &lt;element name="DebtorAdditionalData" type="{}DebtorAdditionalDataComplexType" minOccurs="0"/>
 *         &lt;element name="Doc" type="{}DocComplexType" minOccurs="0"/>
 *         &lt;element name="DebtorWallet" type="{}DebtorWalletComplexType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}RequestAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPaymentTransactionRequestType", propOrder = {
    "paymentReference",
    "debtorReference",
    "description",
    "skinID",
    "currency",
    "amount",
    "minAmount",
    "maxAmount",
    "isAmountMutable",
    "amountArray",
    "dueDateTime",
    "debtorReturnURL",
    "debtorAdditionalData",
    "doc",
    "debtorWallet"
})
public class EPaymentTransactionRequestType {

    @XmlElement(name = "PaymentReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentReference;
    @XmlElement(name = "DebtorReference", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorReference;
    @XmlElement(name = "Description", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String description;
    @XmlElement(name = "SkinID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String skinID;
    @XmlElement(name = "Currency", defaultValue = "EUR")
    @XmlSchemaType(name = "token")
    protected CurrencySimpleType currency;
    @XmlElement(name = "Amount")
    @JsonSerialize(using = BigDecimalMoneyJsonSerializer.class)
    protected BigDecimal amount;
    @XmlElement(name = "MinAmount")
    protected String minAmount;
    @XmlElement(name = "MaxAmount")
    protected String maxAmount;
    @XmlElement(name = "IsAmountMutable", defaultValue = "0")
    protected Boolean isAmountMutable;
    @XmlElement(name = "AmountArray")
    protected AmountArrayComplexType amountArray;
    @XmlElement(name = "DueDateTime", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String dueDateTime;
    @XmlElement(name = "DebtorReturnURL")
    protected DebtorReturnURLComplexType debtorReturnURL;
    @XmlElement(name = "DebtorAdditionalData")
    protected DebtorAdditionalDataComplexType debtorAdditionalData;
    @XmlElement(name = "Doc")
    protected DocComplexType doc;
    @XmlElement(name = "DebtorWallet")
    protected DebtorWalletComplexType debtorWallet;
    @XmlAttribute(name = "entranceCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String entranceCode;
    @XmlAttribute(name = "brandID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String brandID;
    @XmlAttribute(name = "documentType")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String documentType;
    @XmlAttribute(name = "sendOption")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String sendOption;
    @XmlAttribute(name = "language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String language;

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the skinID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkinID() {
        return skinID;
    }

    /**
     * Sets the value of the skinID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkinID(String value) {
        this.skinID = value;
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
     * Gets the value of the minAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinAmount() {
        return minAmount;
    }

    /**
     * Sets the value of the minAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinAmount(String value) {
        this.minAmount = value;
    }

    /**
     * Gets the value of the maxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxAmount() {
        return maxAmount;
    }

    /**
     * Sets the value of the maxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxAmount(String value) {
        this.maxAmount = value;
    }

    /**
     * Gets the value of the isAmountMutable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAmountMutable() {
        return isAmountMutable;
    }

    /**
     * Sets the value of the isAmountMutable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAmountMutable(Boolean value) {
        this.isAmountMutable = value;
    }

    /**
     * Gets the value of the amountArray property.
     * 
     * @return
     *     possible object is
     *     {@link AmountArrayComplexType }
     *     
     */
    public AmountArrayComplexType getAmountArray() {
        return amountArray;
    }

    /**
     * Sets the value of the amountArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountArrayComplexType }
     *     
     */
    public void setAmountArray(AmountArrayComplexType value) {
        this.amountArray = value;
    }

    /**
     * Gets the value of the dueDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Sets the value of the dueDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDueDateTime(String value) {
        this.dueDateTime = value;
    }

    /**
     * Gets the value of the debtorReturnURL property.
     * 
     * @return
     *     possible object is
     *     {@link DebtorReturnURLComplexType }
     *     
     */
    public DebtorReturnURLComplexType getDebtorReturnURL() {
        return debtorReturnURL;
    }

    /**
     * Sets the value of the debtorReturnURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebtorReturnURLComplexType }
     *     
     */
    public void setDebtorReturnURL(DebtorReturnURLComplexType value) {
        this.debtorReturnURL = value;
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
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link DocComplexType }
     *     
     */
    public DocComplexType getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocComplexType }
     *     
     */
    public void setDoc(DocComplexType value) {
        this.doc = value;
    }

    /**
     * Gets the value of the debtorWallet property.
     * 
     * @return
     *     possible object is
     *     {@link DebtorWalletComplexType }
     *     
     */
    public DebtorWalletComplexType getDebtorWallet() {
        return debtorWallet;
    }

    /**
     * Sets the value of the debtorWallet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebtorWalletComplexType }
     *     
     */
    public void setDebtorWallet(DebtorWalletComplexType value) {
        this.debtorWallet = value;
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

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentType(String value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the sendOption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendOption() {
        return sendOption;
    }

    /**
     * Sets the value of the sendOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendOption(String value) {
        this.sendOption = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        if (language == null) {
            return "nl";
        } else {
            return language;
        }
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
