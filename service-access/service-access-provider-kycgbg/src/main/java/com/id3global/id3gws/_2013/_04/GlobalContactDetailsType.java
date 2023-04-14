
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalContactDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalContactDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LandTelephone" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalLandTelephone" minOccurs="0"/&gt;
 *         &lt;element name="MobileTelephone" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMobileTelephone" minOccurs="0"/&gt;
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="WorkTelephone" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalWorkTelephone" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalContactDetails", propOrder = {
    "landTelephone",
    "mobileTelephone",
    "email",
    "workTelephone"
})
public class GlobalContactDetailsType {

    @XmlElementRef(name = "LandTelephone", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalLandTelephoneType> landTelephone;
    @XmlElementRef(name = "MobileTelephone", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMobileTelephoneType> mobileTelephone;
    @XmlElementRef(name = "Email", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> email;
    @XmlElementRef(name = "WorkTelephone", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalWorkTelephoneType> workTelephone;

    /**
     * Gets the value of the landTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalLandTelephoneType }{@code >}
     *     
     */
    public JAXBElement<GlobalLandTelephoneType> getLandTelephone() {
        return landTelephone;
    }

    /**
     * Sets the value of the landTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalLandTelephoneType }{@code >}
     *     
     */
    public void setLandTelephone(JAXBElement<GlobalLandTelephoneType> value) {
        this.landTelephone = value;
    }

    /**
     * Gets the value of the mobileTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMobileTelephoneType }{@code >}
     *     
     */
    public JAXBElement<GlobalMobileTelephoneType> getMobileTelephone() {
        return mobileTelephone;
    }

    /**
     * Sets the value of the mobileTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMobileTelephoneType }{@code >}
     *     
     */
    public void setMobileTelephone(JAXBElement<GlobalMobileTelephoneType> value) {
        this.mobileTelephone = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<String> value) {
        this.email = value;
    }

    /**
     * Gets the value of the workTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalWorkTelephoneType }{@code >}
     *     
     */
    public JAXBElement<GlobalWorkTelephoneType> getWorkTelephone() {
        return workTelephone;
    }

    /**
     * Sets the value of the workTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalWorkTelephoneType }{@code >}
     *     
     */
    public void setWorkTelephone(JAXBElement<GlobalWorkTelephoneType> value) {
        this.workTelephone = value;
    }

}
