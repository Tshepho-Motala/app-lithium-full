
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalAustralia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalAustralia"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ShortPassport" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalShortPassport" minOccurs="0"/&gt;
 *         &lt;element name="Medicare" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMedicare" minOccurs="0"/&gt;
 *         &lt;element name="DrivingLicence" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAustraliaDrivingLicence" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalAustralia", propOrder = {
    "shortPassport",
    "medicare",
    "drivingLicence"
})
public class GlobalAustraliaType {

    @XmlElementRef(name = "ShortPassport", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalShortPassportType> shortPassport;
    @XmlElementRef(name = "Medicare", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMedicareType> medicare;
    @XmlElementRef(name = "DrivingLicence", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAustraliaDrivingLicenceType> drivingLicence;

    /**
     * Gets the value of the shortPassport property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalShortPassportType }{@code >}
     *     
     */
    public JAXBElement<GlobalShortPassportType> getShortPassport() {
        return shortPassport;
    }

    /**
     * Sets the value of the shortPassport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalShortPassportType }{@code >}
     *     
     */
    public void setShortPassport(JAXBElement<GlobalShortPassportType> value) {
        this.shortPassport = value;
    }

    /**
     * Gets the value of the medicare property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMedicareType }{@code >}
     *     
     */
    public JAXBElement<GlobalMedicareType> getMedicare() {
        return medicare;
    }

    /**
     * Sets the value of the medicare property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMedicareType }{@code >}
     *     
     */
    public void setMedicare(JAXBElement<GlobalMedicareType> value) {
        this.medicare = value;
    }

    /**
     * Gets the value of the drivingLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAustraliaDrivingLicenceType }{@code >}
     *     
     */
    public JAXBElement<GlobalAustraliaDrivingLicenceType> getDrivingLicence() {
        return drivingLicence;
    }

    /**
     * Sets the value of the drivingLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAustraliaDrivingLicenceType }{@code >}
     *     
     */
    public void setDrivingLicence(JAXBElement<GlobalAustraliaDrivingLicenceType> value) {
        this.drivingLicence = value;
    }

}
