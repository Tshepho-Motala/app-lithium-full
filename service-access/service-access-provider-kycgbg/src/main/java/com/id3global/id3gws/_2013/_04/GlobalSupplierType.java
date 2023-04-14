
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalSupplier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalSupplier"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SupplierID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsRemote" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CanCreateAccounts" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Contact1st" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Email1st" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Telephone1st" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Hours1st" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Contact2nd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Email2nd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Telephone2nd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Hours2nd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ContactAccMan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EmailAccMan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TelephoneAccMan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HoursAccMan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResponseP1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResponseP2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResponseP3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ResponseP4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PendingCount" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ActiveCount" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="AutoPasswordChange" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalSupplier", propOrder = {
    "supplierID",
    "name",
    "isRemote",
    "canCreateAccounts",
    "contact1St",
    "email1St",
    "telephone1St",
    "hours1St",
    "contact2Nd",
    "email2Nd",
    "telephone2Nd",
    "hours2Nd",
    "contactAccMan",
    "emailAccMan",
    "telephoneAccMan",
    "hoursAccMan",
    "responseP1",
    "responseP2",
    "responseP3",
    "responseP4",
    "pendingCount",
    "activeCount",
    "autoPasswordChange"
})
public class GlobalSupplierType {

    @XmlElement(name = "SupplierID")
    protected String supplierID;
    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElement(name = "IsRemote")
    protected Boolean isRemote;
    @XmlElement(name = "CanCreateAccounts")
    protected Boolean canCreateAccounts;
    @XmlElementRef(name = "Contact1st", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> contact1St;
    @XmlElementRef(name = "Email1st", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> email1St;
    @XmlElementRef(name = "Telephone1st", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> telephone1St;
    @XmlElementRef(name = "Hours1st", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> hours1St;
    @XmlElementRef(name = "Contact2nd", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> contact2Nd;
    @XmlElementRef(name = "Email2nd", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> email2Nd;
    @XmlElementRef(name = "Telephone2nd", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> telephone2Nd;
    @XmlElementRef(name = "Hours2nd", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> hours2Nd;
    @XmlElementRef(name = "ContactAccMan", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> contactAccMan;
    @XmlElementRef(name = "EmailAccMan", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> emailAccMan;
    @XmlElementRef(name = "TelephoneAccMan", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> telephoneAccMan;
    @XmlElementRef(name = "HoursAccMan", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> hoursAccMan;
    @XmlElementRef(name = "ResponseP1", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> responseP1;
    @XmlElementRef(name = "ResponseP2", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> responseP2;
    @XmlElementRef(name = "ResponseP3", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> responseP3;
    @XmlElementRef(name = "ResponseP4", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> responseP4;
    @XmlElement(name = "PendingCount")
    @XmlSchemaType(name = "unsignedInt")
    protected Long pendingCount;
    @XmlElement(name = "ActiveCount")
    @XmlSchemaType(name = "unsignedInt")
    protected Long activeCount;
    @XmlElement(name = "AutoPasswordChange")
    protected Boolean autoPasswordChange;

    /**
     * Gets the value of the supplierID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierID() {
        return supplierID;
    }

    /**
     * Sets the value of the supplierID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierID(String value) {
        this.supplierID = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = value;
    }

    /**
     * Gets the value of the isRemote property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRemote() {
        return isRemote;
    }

    /**
     * Sets the value of the isRemote property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRemote(Boolean value) {
        this.isRemote = value;
    }

    /**
     * Gets the value of the canCreateAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanCreateAccounts() {
        return canCreateAccounts;
    }

    /**
     * Sets the value of the canCreateAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanCreateAccounts(Boolean value) {
        this.canCreateAccounts = value;
    }

    /**
     * Gets the value of the contact1St property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getContact1St() {
        return contact1St;
    }

    /**
     * Sets the value of the contact1St property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setContact1St(JAXBElement<String> value) {
        this.contact1St = value;
    }

    /**
     * Gets the value of the email1St property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail1St() {
        return email1St;
    }

    /**
     * Sets the value of the email1St property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail1St(JAXBElement<String> value) {
        this.email1St = value;
    }

    /**
     * Gets the value of the telephone1St property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTelephone1St() {
        return telephone1St;
    }

    /**
     * Sets the value of the telephone1St property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTelephone1St(JAXBElement<String> value) {
        this.telephone1St = value;
    }

    /**
     * Gets the value of the hours1St property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHours1St() {
        return hours1St;
    }

    /**
     * Sets the value of the hours1St property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHours1St(JAXBElement<String> value) {
        this.hours1St = value;
    }

    /**
     * Gets the value of the contact2Nd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getContact2Nd() {
        return contact2Nd;
    }

    /**
     * Sets the value of the contact2Nd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setContact2Nd(JAXBElement<String> value) {
        this.contact2Nd = value;
    }

    /**
     * Gets the value of the email2Nd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail2Nd() {
        return email2Nd;
    }

    /**
     * Sets the value of the email2Nd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail2Nd(JAXBElement<String> value) {
        this.email2Nd = value;
    }

    /**
     * Gets the value of the telephone2Nd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTelephone2Nd() {
        return telephone2Nd;
    }

    /**
     * Sets the value of the telephone2Nd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTelephone2Nd(JAXBElement<String> value) {
        this.telephone2Nd = value;
    }

    /**
     * Gets the value of the hours2Nd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHours2Nd() {
        return hours2Nd;
    }

    /**
     * Sets the value of the hours2Nd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHours2Nd(JAXBElement<String> value) {
        this.hours2Nd = value;
    }

    /**
     * Gets the value of the contactAccMan property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getContactAccMan() {
        return contactAccMan;
    }

    /**
     * Sets the value of the contactAccMan property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setContactAccMan(JAXBElement<String> value) {
        this.contactAccMan = value;
    }

    /**
     * Gets the value of the emailAccMan property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmailAccMan() {
        return emailAccMan;
    }

    /**
     * Sets the value of the emailAccMan property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmailAccMan(JAXBElement<String> value) {
        this.emailAccMan = value;
    }

    /**
     * Gets the value of the telephoneAccMan property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTelephoneAccMan() {
        return telephoneAccMan;
    }

    /**
     * Sets the value of the telephoneAccMan property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTelephoneAccMan(JAXBElement<String> value) {
        this.telephoneAccMan = value;
    }

    /**
     * Gets the value of the hoursAccMan property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHoursAccMan() {
        return hoursAccMan;
    }

    /**
     * Sets the value of the hoursAccMan property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHoursAccMan(JAXBElement<String> value) {
        this.hoursAccMan = value;
    }

    /**
     * Gets the value of the responseP1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResponseP1() {
        return responseP1;
    }

    /**
     * Sets the value of the responseP1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResponseP1(JAXBElement<String> value) {
        this.responseP1 = value;
    }

    /**
     * Gets the value of the responseP2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResponseP2() {
        return responseP2;
    }

    /**
     * Sets the value of the responseP2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResponseP2(JAXBElement<String> value) {
        this.responseP2 = value;
    }

    /**
     * Gets the value of the responseP3 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResponseP3() {
        return responseP3;
    }

    /**
     * Sets the value of the responseP3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResponseP3(JAXBElement<String> value) {
        this.responseP3 = value;
    }

    /**
     * Gets the value of the responseP4 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResponseP4() {
        return responseP4;
    }

    /**
     * Sets the value of the responseP4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResponseP4(JAXBElement<String> value) {
        this.responseP4 = value;
    }

    /**
     * Gets the value of the pendingCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPendingCount() {
        return pendingCount;
    }

    /**
     * Sets the value of the pendingCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPendingCount(Long value) {
        this.pendingCount = value;
    }

    /**
     * Gets the value of the activeCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getActiveCount() {
        return activeCount;
    }

    /**
     * Sets the value of the activeCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setActiveCount(Long value) {
        this.activeCount = value;
    }

    /**
     * Gets the value of the autoPasswordChange property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoPasswordChange() {
        return autoPasswordChange;
    }

    /**
     * Sets the value of the autoPasswordChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoPasswordChange(Boolean value) {
        this.autoPasswordChange = value;
    }

}
