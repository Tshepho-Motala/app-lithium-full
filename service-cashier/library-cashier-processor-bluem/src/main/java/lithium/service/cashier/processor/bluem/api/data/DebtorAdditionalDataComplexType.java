
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for DebtorAdditionalDataComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DebtorAdditionalDataComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EmailAddress" type="{}EmailSimpleType" minOccurs="0"/>
 *         &lt;element name="MobilePhoneNumber" type="{}PhoneNumberSimpleType" minOccurs="0"/>
 *         &lt;element name="InvoiceNumber" type="{}InvoiceNumberSimpleType" minOccurs="0"/>
 *         &lt;element name="InvoiceDate" type="{}DateSimpleType" minOccurs="0"/>
 *         &lt;element name="CustomerName" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *         &lt;element name="AttentionOf" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *         &lt;element name="Salutation" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *         &lt;element name="CustomerAddressLine1" type="{}TokenLength128SimpleType" minOccurs="0"/>
 *         &lt;element name="CustomerAddressLine2" type="{}TokenLength128SimpleType" minOccurs="0"/>
 *         &lt;element name="DynamicData" type="{}DynamicDataComplexType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DebtorAdditionalDataComplexType", propOrder = {
    "emailAddress",
    "mobilePhoneNumber",
    "invoiceNumber",
    "invoiceDate",
    "customerName",
    "attentionOf",
    "salutation",
    "customerAddressLine1",
    "customerAddressLine2",
    "dynamicData"
})
public class DebtorAdditionalDataComplexType {

    @XmlElement(name = "EmailAddress")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String emailAddress;
    @XmlElement(name = "MobilePhoneNumber")
    protected String mobilePhoneNumber;
    @XmlElement(name = "InvoiceNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String invoiceNumber;
    @XmlElement(name = "InvoiceDate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String invoiceDate;
    @XmlElement(name = "CustomerName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String customerName;
    @XmlElement(name = "AttentionOf")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String attentionOf;
    @XmlElement(name = "Salutation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String salutation;
    @XmlElement(name = "CustomerAddressLine1")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String customerAddressLine1;
    @XmlElement(name = "CustomerAddressLine2")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String customerAddressLine2;
    @XmlElement(name = "DynamicData")
    protected DynamicDataComplexType dynamicData;

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the mobilePhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    /**
     * Sets the value of the mobilePhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhoneNumber(String value) {
        this.mobilePhoneNumber = value;
    }

    /**
     * Gets the value of the invoiceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the value of the invoiceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceNumber(String value) {
        this.invoiceNumber = value;
    }

    /**
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceDate(String value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerName(String value) {
        this.customerName = value;
    }

    /**
     * Gets the value of the attentionOf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttentionOf() {
        return attentionOf;
    }

    /**
     * Sets the value of the attentionOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttentionOf(String value) {
        this.attentionOf = value;
    }

    /**
     * Gets the value of the salutation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalutation() {
        return salutation;
    }

    /**
     * Sets the value of the salutation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalutation(String value) {
        this.salutation = value;
    }

    /**
     * Gets the value of the customerAddressLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerAddressLine1() {
        return customerAddressLine1;
    }

    /**
     * Sets the value of the customerAddressLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerAddressLine1(String value) {
        this.customerAddressLine1 = value;
    }

    /**
     * Gets the value of the customerAddressLine2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerAddressLine2() {
        return customerAddressLine2;
    }

    /**
     * Sets the value of the customerAddressLine2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerAddressLine2(String value) {
        this.customerAddressLine2 = value;
    }

    /**
     * Gets the value of the dynamicData property.
     * 
     * @return
     *     possible object is
     *     {@link DynamicDataComplexType }
     *     
     */
    public DynamicDataComplexType getDynamicData() {
        return dynamicData;
    }

    /**
     * Sets the value of the dynamicData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DynamicDataComplexType }
     *     
     */
    public void setDynamicData(DynamicDataComplexType value) {
        this.dynamicData = value;
    }

}
