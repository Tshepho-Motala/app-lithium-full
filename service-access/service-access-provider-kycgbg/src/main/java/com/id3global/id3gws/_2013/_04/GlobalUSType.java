
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalUS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalUS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DrivingLicense" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUSDrivingLicense" minOccurs="0"/&gt;
 *         &lt;element name="SocialSecurity" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUSSocialSecurity" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalUS", propOrder = {
    "drivingLicense",
    "socialSecurity"
})
public class GlobalUSType {

    @XmlElementRef(name = "DrivingLicense", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUSDrivingLicenseType> drivingLicense;
    @XmlElementRef(name = "SocialSecurity", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUSSocialSecurityType> socialSecurity;

    /**
     * Gets the value of the drivingLicense property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSDrivingLicenseType }{@code >}
     *     
     */
    public JAXBElement<GlobalUSDrivingLicenseType> getDrivingLicense() {
        return drivingLicense;
    }

    /**
     * Sets the value of the drivingLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSDrivingLicenseType }{@code >}
     *     
     */
    public void setDrivingLicense(JAXBElement<GlobalUSDrivingLicenseType> value) {
        this.drivingLicense = value;
    }

    /**
     * Gets the value of the socialSecurity property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSSocialSecurityType }{@code >}
     *     
     */
    public JAXBElement<GlobalUSSocialSecurityType> getSocialSecurity() {
        return socialSecurity;
    }

    /**
     * Sets the value of the socialSecurity property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSSocialSecurityType }{@code >}
     *     
     */
    public void setSocialSecurity(JAXBElement<GlobalUSSocialSecurityType> value) {
        this.socialSecurity = value;
    }

}
