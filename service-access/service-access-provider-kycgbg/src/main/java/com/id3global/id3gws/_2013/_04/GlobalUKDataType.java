
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalUKData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalUKData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Passport" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKPassport" minOccurs="0"/&gt;
 *         &lt;element name="DrivingLicence" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKDrivingLicence" minOccurs="0"/&gt;
 *         &lt;element name="NationalInsuranceNumber" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKNationalInsuranceNumber" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalUKData", propOrder = {
    "passport",
    "drivingLicence",
    "nationalInsuranceNumber"
})
public class GlobalUKDataType {

    @XmlElementRef(name = "Passport", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUKPassportType> passport;
    @XmlElementRef(name = "DrivingLicence", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUKDrivingLicenceType> drivingLicence;
    @XmlElementRef(name = "NationalInsuranceNumber", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUKNationalInsuranceNumberType> nationalInsuranceNumber;

    /**
     * Gets the value of the passport property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKPassportType }{@code >}
     *     
     */
    public JAXBElement<GlobalUKPassportType> getPassport() {
        return passport;
    }

    /**
     * Sets the value of the passport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKPassportType }{@code >}
     *     
     */
    public void setPassport(JAXBElement<GlobalUKPassportType> value) {
        this.passport = value;
    }

    /**
     * Gets the value of the drivingLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKDrivingLicenceType }{@code >}
     *     
     */
    public JAXBElement<GlobalUKDrivingLicenceType> getDrivingLicence() {
        return drivingLicence;
    }

    /**
     * Sets the value of the drivingLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKDrivingLicenceType }{@code >}
     *     
     */
    public void setDrivingLicence(JAXBElement<GlobalUKDrivingLicenceType> value) {
        this.drivingLicence = value;
    }

    /**
     * Gets the value of the nationalInsuranceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKNationalInsuranceNumberType }{@code >}
     *     
     */
    public JAXBElement<GlobalUKNationalInsuranceNumberType> getNationalInsuranceNumber() {
        return nationalInsuranceNumber;
    }

    /**
     * Sets the value of the nationalInsuranceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKNationalInsuranceNumberType }{@code >}
     *     
     */
    public void setNationalInsuranceNumber(JAXBElement<GlobalUKNationalInsuranceNumberType> value) {
        this.nationalInsuranceNumber = value;
    }

}
