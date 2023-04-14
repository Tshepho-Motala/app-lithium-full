
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfdateTime;


/**
 * <p>Java class for GlobalCaseNotificationJob complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseNotificationJob"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NotificationType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TokenRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Recipient" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SentDates" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfdateTime" minOccurs="0"/&gt;
 *         &lt;element name="RecipientOrgID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="RecipientDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderOrgName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Dismissed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseNotificationJob", propOrder = {
    "notificationType",
    "state",
    "tokenRequired",
    "recipient",
    "sentDates",
    "recipientOrgID",
    "recipientDomain",
    "senderName",
    "senderOrgName",
    "dismissed"
})
public class GlobalCaseNotificationJobType {

    @XmlElement(name = "NotificationType")
    protected Integer notificationType;
    @XmlElement(name = "State")
    protected Integer state;
    @XmlElement(name = "TokenRequired")
    protected Boolean tokenRequired;
    @XmlElement(name = "Recipient")
    protected Integer recipient;
    @XmlElementRef(name = "SentDates", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfdateTime> sentDates;
    @XmlElementRef(name = "RecipientOrgID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> recipientOrgID;
    @XmlElementRef(name = "RecipientDomain", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> recipientDomain;
    @XmlElementRef(name = "SenderName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> senderName;
    @XmlElementRef(name = "SenderOrgName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> senderOrgName;
    @XmlElement(name = "Dismissed")
    protected Boolean dismissed;

    /**
     * Gets the value of the notificationType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the value of the notificationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotificationType(Integer value) {
        this.notificationType = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setState(Integer value) {
        this.state = value;
    }

    /**
     * Gets the value of the tokenRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTokenRequired() {
        return tokenRequired;
    }

    /**
     * Sets the value of the tokenRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTokenRequired(Boolean value) {
        this.tokenRequired = value;
    }

    /**
     * Gets the value of the recipient property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRecipient() {
        return recipient;
    }

    /**
     * Sets the value of the recipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRecipient(Integer value) {
        this.recipient = value;
    }

    /**
     * Gets the value of the sentDates property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfdateTime }{@code >}
     *     
     */
    public JAXBElement<ArrayOfdateTime> getSentDates() {
        return sentDates;
    }

    /**
     * Sets the value of the sentDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfdateTime }{@code >}
     *     
     */
    public void setSentDates(JAXBElement<ArrayOfdateTime> value) {
        this.sentDates = value;
    }

    /**
     * Gets the value of the recipientOrgID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRecipientOrgID() {
        return recipientOrgID;
    }

    /**
     * Sets the value of the recipientOrgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRecipientOrgID(JAXBElement<String> value) {
        this.recipientOrgID = value;
    }

    /**
     * Gets the value of the recipientDomain property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRecipientDomain() {
        return recipientDomain;
    }

    /**
     * Sets the value of the recipientDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRecipientDomain(JAXBElement<String> value) {
        this.recipientDomain = value;
    }

    /**
     * Gets the value of the senderName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSenderName() {
        return senderName;
    }

    /**
     * Sets the value of the senderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSenderName(JAXBElement<String> value) {
        this.senderName = value;
    }

    /**
     * Gets the value of the senderOrgName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSenderOrgName() {
        return senderOrgName;
    }

    /**
     * Sets the value of the senderOrgName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSenderOrgName(JAXBElement<String> value) {
        this.senderOrgName = value;
    }

    /**
     * Gets the value of the dismissed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDismissed() {
        return dismissed;
    }

    /**
     * Sets the value of the dismissed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDismissed(Boolean value) {
        this.dismissed = value;
    }

}
