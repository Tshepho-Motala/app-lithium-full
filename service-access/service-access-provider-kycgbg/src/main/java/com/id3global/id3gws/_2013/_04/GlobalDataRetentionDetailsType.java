
package com.id3global.id3gws._2013._04;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for GlobalDataRetentionDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalDataRetentionDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RetentionDays" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedAccountID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedUsername" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedDomainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedDatetime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="EffectiveFromDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDataRetentionDetails", propOrder = {
    "retentionDays",
    "updatedAccountID",
    "updatedUsername",
    "updatedDomainName",
    "updatedDatetime",
    "effectiveFromDateTime"
})
public class GlobalDataRetentionDetailsType {

    @XmlElementRef(name = "RetentionDays", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> retentionDays;
    @XmlElement(name = "UpdatedAccountID")
    protected String updatedAccountID;
    @XmlElementRef(name = "UpdatedUsername", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> updatedUsername;
    @XmlElementRef(name = "UpdatedDomainName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> updatedDomainName;
    @XmlElement(name = "UpdatedDatetime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date updatedDatetime;
    @XmlElementRef(name = "EffectiveFromDateTime", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> effectiveFromDateTime;

    /**
     * Gets the value of the retentionDays property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getRetentionDays() {
        return retentionDays;
    }

    /**
     * Sets the value of the retentionDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setRetentionDays(JAXBElement<Integer> value) {
        this.retentionDays = value;
    }

    /**
     * Gets the value of the updatedAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedAccountID() {
        return updatedAccountID;
    }

    /**
     * Sets the value of the updatedAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedAccountID(String value) {
        this.updatedAccountID = value;
    }

    /**
     * Gets the value of the updatedUsername property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUpdatedUsername() {
        return updatedUsername;
    }

    /**
     * Sets the value of the updatedUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUpdatedUsername(JAXBElement<String> value) {
        this.updatedUsername = value;
    }

    /**
     * Gets the value of the updatedDomainName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUpdatedDomainName() {
        return updatedDomainName;
    }

    /**
     * Sets the value of the updatedDomainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUpdatedDomainName(JAXBElement<String> value) {
        this.updatedDomainName = value;
    }

    /**
     * Gets the value of the updatedDatetime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    /**
     * Sets the value of the updatedDatetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedDatetime(Date value) {
        this.updatedDatetime = value;
    }

    /**
     * Gets the value of the effectiveFromDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getEffectiveFromDateTime() {
        return effectiveFromDateTime;
    }

    /**
     * Sets the value of the effectiveFromDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setEffectiveFromDateTime(JAXBElement<Date> value) {
        this.effectiveFromDateTime = value;
    }

}
