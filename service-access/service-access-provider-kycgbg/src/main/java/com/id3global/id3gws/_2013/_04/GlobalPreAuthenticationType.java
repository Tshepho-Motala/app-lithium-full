
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for GlobalPreAuthentication complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalPreAuthentication"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MatchDocument" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MatchNameAddresses" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/&gt;
 *         &lt;element name="DuplicateMatchNameAddress" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TimespanHours" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TimespanDays" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Duplicates" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ResponseMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RestrictByProfile" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="RestrictByUser" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalPreAuthentication", propOrder = {
    "name",
    "matchDocument",
    "matchNameAddresses",
    "duplicateMatchNameAddress",
    "timespanHours",
    "timespanDays",
    "duplicates",
    "responseMessage",
    "restrictByProfile",
    "restrictByUser"
})
public class GlobalPreAuthenticationType {

    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElementRef(name = "MatchDocument", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> matchDocument;
    @XmlElementRef(name = "MatchNameAddresses", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> matchNameAddresses;
    @XmlElement(name = "DuplicateMatchNameAddress")
    @XmlSchemaType(name = "unsignedInt")
    protected Long duplicateMatchNameAddress;
    @XmlElement(name = "TimespanHours")
    @XmlSchemaType(name = "unsignedInt")
    protected Long timespanHours;
    @XmlElement(name = "TimespanDays")
    @XmlSchemaType(name = "unsignedInt")
    protected Long timespanDays;
    @XmlElement(name = "Duplicates")
    @XmlSchemaType(name = "unsignedInt")
    protected Long duplicates;
    @XmlElementRef(name = "ResponseMessage", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> responseMessage;
    @XmlElement(name = "RestrictByProfile")
    protected Boolean restrictByProfile;
    @XmlElement(name = "RestrictByUser")
    protected Boolean restrictByUser;

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
     * Gets the value of the matchDocument property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMatchDocument() {
        return matchDocument;
    }

    /**
     * Sets the value of the matchDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMatchDocument(JAXBElement<String> value) {
        this.matchDocument = value;
    }

    /**
     * Gets the value of the matchNameAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getMatchNameAddresses() {
        return matchNameAddresses;
    }

    /**
     * Sets the value of the matchNameAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setMatchNameAddresses(JAXBElement<ArrayOfstring> value) {
        this.matchNameAddresses = value;
    }

    /**
     * Gets the value of the duplicateMatchNameAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDuplicateMatchNameAddress() {
        return duplicateMatchNameAddress;
    }

    /**
     * Sets the value of the duplicateMatchNameAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDuplicateMatchNameAddress(Long value) {
        this.duplicateMatchNameAddress = value;
    }

    /**
     * Gets the value of the timespanHours property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTimespanHours() {
        return timespanHours;
    }

    /**
     * Sets the value of the timespanHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimespanHours(Long value) {
        this.timespanHours = value;
    }

    /**
     * Gets the value of the timespanDays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTimespanDays() {
        return timespanDays;
    }

    /**
     * Sets the value of the timespanDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimespanDays(Long value) {
        this.timespanDays = value;
    }

    /**
     * Gets the value of the duplicates property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDuplicates() {
        return duplicates;
    }

    /**
     * Sets the value of the duplicates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDuplicates(Long value) {
        this.duplicates = value;
    }

    /**
     * Gets the value of the responseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getResponseMessage() {
        return responseMessage;
    }

    /**
     * Sets the value of the responseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setResponseMessage(JAXBElement<String> value) {
        this.responseMessage = value;
    }

    /**
     * Gets the value of the restrictByProfile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRestrictByProfile() {
        return restrictByProfile;
    }

    /**
     * Sets the value of the restrictByProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRestrictByProfile(Boolean value) {
        this.restrictByProfile = value;
    }

    /**
     * Gets the value of the restrictByUser property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRestrictByUser() {
        return restrictByUser;
    }

    /**
     * Sets the value of the restrictByUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRestrictByUser(Boolean value) {
        this.restrictByUser = value;
    }

}
